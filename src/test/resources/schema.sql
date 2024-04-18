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

create table product_type
(
    id   uuid default gen_random_uuid() not null
        constraint product_type_pk
            primary key,
    name varchar                        not null
        constraint unique_product_type_name
            unique
);

create table product
(
    id              uuid default gen_random_uuid() not null
        constraint product_pk
            primary key,
    name            varchar                        not null,
    type            uuid                           not null
        constraint product_type_id_fk
            references product_type,
    price           integer                        not null,
    condition       integer                        not null
        constraint check_condition_range
            check ((condition >= 0) AND (condition <= 4)),
    is_purchased    boolean                        not null,
    description     text                           not null,
    seller          uuid                           not null
        constraint product_seller_fk
            references account,
    buyer           uuid
        constraint product_buyer_fk
            references account,
    color           integer,
    production_year integer
        constraint check_year
            check ((production_year >= 2000) AND (production_year <= 2100)),
    constraint check_purchasable
        check ((buyer IS NULL) AND (is_purchased IS FALSE))
);

create index product_buyer_index
    on product (buyer);

create index product_is_purchased_index
    on product (is_purchased);

create index product_seller_index
    on product (seller);

create index product_type_index
    on product (type);

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
            references product_type,
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