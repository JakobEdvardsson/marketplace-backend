create table account
(
    id            uuid default gen_random_uuid() not null
        constraint account_pk
            primary key,
    first_name    varchar                        not null,
    last_name     varchar                        not null,
    date_of_birth date                           not null,
    email         varchar(255)                   not null
        constraint account_unique_email
            unique,
    password      varchar(128)                   not null,
    username      varchar(64)                    not null
        constraint account_unique_username
            unique
);

create table inbox
(
    receiver_id uuid                           not null
        constraint inbox_receiver_fk
            references account,
    message     text                           not null,
    is_read     boolean                        not null,
    id          uuid default gen_random_uuid() not null
        constraint inbox_pk
            primary key
);

create index inbox_receiver_id_index
    on inbox (receiver_id);

create table product_category
(
    id   uuid default gen_random_uuid() not null
        constraint product_category_pk
            primary key,
    name varchar                        not null
        constraint unique_product_type_name
            unique
);

create table product
(
    id               uuid                     default gen_random_uuid() not null
        constraint product_pk
            primary key,
    name             varchar                                            not null,
    product_category uuid                                               not null
        constraint product_type_id_fk
            references product_category,
    price            integer                                            not null,
    condition        integer                                            not null
        constraint check_condition_range
            check ((condition >= 0) AND (condition <= 4)),
    is_purchased     boolean                                            not null,
    description      text                                               not null,
    seller           uuid                                               not null
        constraint product_seller_fk
            references account,
    buyer            uuid
        constraint product_buyer_fk
            references account,
    color            integer,
    production_year  integer
        constraint check_year
            check ((production_year >= 2000) AND (production_year <= 2100)),
    created_at       timestamp with time zone default now()             not null,
    constraint check_valid_purchase_status
        check (((buyer IS NULL) AND (is_purchased IS FALSE)) OR
               ((buyer IS NOT NULL) AND (is_purchased IS TRUE)))
);

create index product_buyer_index
    on product (buyer);

create index product_is_purchased_index
    on product (is_purchased);

create index product_seller_index
    on product (seller);

create index product_product_category_index
    on product (product_category);

create table product_image
(
    product_id uuid                           not null
        constraint product_id_fk
            references product,
    image_url  varchar                        not null,
    id         uuid default gen_random_uuid() not null
        constraint product_image_pk
            primary key
);

create index product_image_product_id_index
    on product_image (product_id);

create table "order"
(
    order_id         uuid default gen_random_uuid() not null
        constraint order_pk
            primary key,
    time_of_purchase timestamp with time zone       not null,
    buyer_id         uuid                           not null
        constraint order_account_id_fk
            references account
);

create table order_item
(
    order_id   uuid                           not null
        constraint order_item_order_history_order_id_fk
            references "order",
    product_id uuid                           not null
        constraint order_item_product_id_fk
            references product,
    id         uuid default gen_random_uuid() not null
        constraint order_item_pk
            primary key
);

create index order_item_order_id_index
    on order_item (order_id);

create index order_buyer_id_index
    on "order" (buyer_id);

create table watchlist
(
    product_type_id uuid                           not null
        constraint watchlist_product_type_id_fk
            references product_category,
    subscriber_id   uuid                           not null
        constraint watchlist_account_id_fk
            references account,
    id              uuid default gen_random_uuid() not null
        constraint watchlist_pk
            primary key
);

create index watchlist_product_type_id_index
    on watchlist (product_type_id);

create index watchlist_subscriber_id_index
    on watchlist (subscriber_id);

create function transfer_relations_before_delete() returns trigger
    language plpgsql
as
$$
BEGIN
    -- This logic will be executed before the DELETE operation
    UPDATE product SET seller = uuid 'dc254b85-6610-43c9-9f48-77a80b798158'
                   WHERE seller = OLD.id;
    UPDATE product SET buyer = uuid 'dc254b85-6610-43c9-9f48-77a80b798158'
                    WHERE buyer = OLD.id;
    RETURN OLD;
END;
$$;

create trigger before_account_delete
    before delete
    on account
    for each row
execute procedure transfer_relations_before_delete();

create function check_if_product_is_purchasable() returns trigger
    language plpgsql
as
$$
BEGIN
    IF OLD.buyer IS NOT NULL AND NEW.buyer != 'dc254b85-6610-43c9-9f48-77a80b798158' THEN
        RAISE EXCEPTION 'Product is already purchased!';
    END IF;
    RETURN NEW;
END;
$$;

create trigger before_update_buyer
    before update
        of buyer
    on product
    for each row
execute procedure check_if_product_is_purchasable();

-- placeholder user, used instead of deleted accounts
INSERT INTO public.account (id, first_name, last_name, date_of_birth, email, password, username) VALUES ('dc254b85-6610-43c9-9f48-77a80b798158', 'deleted', 'deleted', '1970-01-01', 'deleted@mail.com', 'deleted', 'deleted');