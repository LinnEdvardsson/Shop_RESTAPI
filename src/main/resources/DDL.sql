-- vilka kunder som handlar vita adidas
select Specification.color, Specification.brand, Customer.firstName
from CustomerOrder
inner join Customer on CustomerOrder.customerId = Customer.id
inner join OrderedProduct on CustomerOrder.id = OrderedProduct.orderId
inner join Product on OrderedProduct.productId = Product.id
inner join Specification on Product.specId = Specification.id
where Specification.color = 'White' and Specification.brand = 'Adidas' and Specification.shoeSize = '39';

-- produkter i varje kategori, right join för att visa kategorier som inte har tillhörande produkter
select Category.categoryName, count(ProductInCategory.categoryId) as productCount from ProductInCategory 
right join Category on Category.id = ProductInCategory.categoryId
group by Category.categoryName, ProductInCategory.categoryId;

-- totalen varje kund handlat för, left join för att visa kunder som inte lagt någon order
select Customer.firstName, Customer.lastName, sum(OrderedProduct.quantity) as productsOrdered, count(distinct CustomerOrder.id) as ordersPlaced, sum(OrderedProduct.quantity * Specification.price) as total from Customer
left join CustomerOrder on CustomerOrder.customerId = Customer.id
left join OrderedProduct on OrderedProduct.orderId = CustomerOrder.id
left join Product on Product.id = OrderedProduct.productId
left join Specification on Specification.id = Product.specId
group by Customer.id;

-- total försäljning i varje stad
-- mjölby har ej sålts för över 1000
select Location.city, sum(OrderedProduct.quantity * Specification.price) as total from Customer
inner join Location on Location.id = Customer.locationId
inner join CustomerOrder on CustomerOrder.customerId = Customer.id
inner join OrderedProduct on OrderedProduct.orderId = CustomerOrder.id
inner join Product on Product.id = OrderedProduct.productId
inner join Specification on Specification.id = Product.specId
group by Location.id
having total > 1000;

-- top 5 sålda produkter
select Product.productName, Specification.brand, sum(OrderedProduct.quantity) as sold from OrderedProduct
inner join Product on Product.id = OrderedProduct.productId
inner join Specification on Specification.id = Product.specId
group by Product.id
order by sold desc 
limit 5;

-- månad med störst försäljning
-- använder distinct för att räkna unika orderid:n
select sum(OrderedProduct.quantity * Specification.price) as totalSales, count(distinct CustomerOrder.id) as totalOrders, date_format(CustomerOrder.dateOfOrder, '%Y-%M') as month
from CustomerOrder
inner join OrderedProduct on OrderedProduct.orderId = CustomerOrder.id
inner join Product on Product.id = OrderedProduct.productId
inner join Specification on Specification.id = Product.specId
group by month
order by totalSales desc limit 1;

select Inventory.quantity from Inventory
inner join Product on Product.id = Inventory.productId
where Product.productName = 'Samba';

select Specification.shoeSize, Inventory.quantity from Specification
left join Product on Product.specId = Specification.id
left join Inventory on Inventory.productId = Product.id
where Product.productName = 'Samba';

SELECT Product.id, Product.productName, Specification.price, Specification.shoeSize, Specification.color, Specification.brand, Category.id, Category.categoryName
from Product
inner join Specification on Specification.id = Product.specId
inner join ProductInCategory on ProductInCategory.productId = Product.id
inner join Category on Category.id = ProductInCategory.categoryId
where Category.id = ProductInCategory.categoryId;

SELECT ShoppingCart.id as shoppingCartId, ShoppingCart.customerId, CartItem.productId, CartItem.quantity
from ShoppingCart 
inner join CartItem on CartItem.cartId = ShoppingCart.id 
inner join Customer on Customer.id = ShoppingCart.customerId;

select CartItem.quantity from CartItem 
inner join ShoppingCart on id = CartItem.cartId
inner join Product on Product.id = CartItem.productId
where customerId =  > 0 
and Product.id = inProductId

SELECT OrderedProduct.orderId, OrderedProduct.productId, OrderedProduct.quantity
FROM OrderedProduct
INNER JOIN CustomerOrder ON CustomerOrder.id = OrderedProduct.orderId
INNER JOIN Customer ON Customer.id = CustomerOrder.customerId
WHERE CustomerOrder.customerId = 1;

select * from ShoppingCart;
select * from CartItem;
select * from Inventory;
select * from OutOfStock;
select * from CartItem inner join ShoppingCart on ShoppingCart.id = CartItem.cartId where ShoppingCart.id = 1;
select * from CustomerOrder;
select * from OrderedProduct;
select * from Product;
select * from Specification;
select * from Customer;



select * from CustomerOrder;
select * from OrderedProduct;
select * from Inventory;
select * from OutOfStock;
select * from CartItem;

-- lägg till ny produkt i Pers kundvagn (productId 7, Leather Fox). --> 
select * from CartItem;
call AddToCart(1, 1, 7);
-- lägg till en till av befintlig vara i Pers kundvagn (productId 1, Dr Martens Jadon) --> INNAN beställning visa Inventory och CartItem
select * from CartItem;
select * from Inventory;
call AddToCart(1, 1, 7);
select * from Inventory;
select * from CartItem;
-- lägg beställning på Pers kundvagn, --> Visa CustomerOrder + CartItem.
call PlaceOrder(1, 1); 
select * from CustomerOrder;
select * from CartItem;
-- Anna lägger order på Brown Breeze som tar slut i lager efter det. --> Visa CustomerOrder + OutofStock + Inventory
select * from OutOfStock;
call PlaceOrder(2, 2);
select * from Inventory;
select * from CustomerOrder;
select * from OutOfStock;
-- Olle försöker lägga order på Brown Breeze som redan är slut nu. --> CustomerOrder, CartItem, Inventory.
call PlaceOrder(3, 3);
select * from CartItem;
-- Rensa Olles Kundvagn --> Visa CartItem
call ClearShoppingCart(3); 
select * from CartItem;

delimiter //
create procedure ClearShoppingCart(IN inShoppingCartId int)
begin
delete from CartItem where CartItem.cartId = inShoppingCartId;
end//
delimiter ;

delimiter //
create trigger After_insert_customer after insert on Customer
for each row
begin
insert into ShoppingCart(customerId) values (NEW.id);
end//
delimiter ;

delimiter //
create trigger After_Inventory_update after update on Inventory
for each row
begin 
	if (NEW.quantity = 0)
	then
    insert into OutOfStock(productId, soldOutSince) values (OLD.productId, current_timestamp());
    end if;
end //
delimiter ;

delimiter //
create procedure PlaceOrder(IN inShoppingCartId int, IN inCustomerId int)
begin
    declare latestOrderId int default 0;

    declare exit handler for sqlexception
    begin
        rollback;
        resignal set message_text = 'Error while placing order';
    end;
    
    declare exit handler for 1645
    begin
    rollback;
    resignal set message_text = 'The order is not completed. '; 
    end;
    
    set autocommit = 0;
    start transaction;
        if exists (select 1 from CartItem inner join Inventory on Inventory.productId = CartItem.productId where CartItem.cartId = inShoppingCartId and CartItem.quantity > Inventory.quantity)
        then 
        rollback;
        resignal;
        end if;

        insert into CustomerOrder(dateOfOrder, customerId) values (current_timestamp, inCustomerId);
        select LAST_INSERT_ID() into latestOrderId;

        insert into OrderedProduct(orderId, productId, quantity) select latestOrderId, CartItem.productId, CartItem.quantity from CartItem
        inner join ShoppingCart on ShoppingCart.id = CartItem.cartId
        where ShoppingCart.customerId = inCustomerId;

        delete from CartItem where CartItem.cartId = inShoppingCartId;

        update Inventory
        inner join OrderedProduct on OrderedProduct.productId = Inventory.productId
        set Inventory.quantity = Inventory.quantity - OrderedProduct.quantity
        where OrderedProduct.orderId = latestOrderId;
    commit;
end//
delimiter ;


delimiter //
create procedure AddToCart(IN inCustomerId int, IN inShoppingCartId int, IN inProductId int)
begin
    declare exit handler for sqlexception
    begin
        rollback;
        resignal set message_text = 'Cannot add product to cart, product is out of stock';
    end;
		
        set autocommit = 0;
		start transaction;
	if (select count(*) from OutOfStock where OutOfStock.productId = inProductId > 0)
    then
    rollback;
    resignal;
    end if;

    if exists (
		select * from CartItem 
		inner join ShoppingCart on ShoppingCart.id = CartItem.cartId 
		inner join Product on Product.id = CartItem.productId 
		where ShoppingCart.customerId = inCustomerId 
		and Product.id = inProductId)
    then
        update CartItem
        inner join ShoppingCart on ShoppingCart.id = CartItem.cartId
        set CartItem.quantity = CartItem.quantity + 1 
        where CartItem.cartId = inShoppingCartId 
        and CartItem.productId = inProductId
        and ShoppingCart.customerId = inCustomerId;
    else
        insert into CartItem(cartId, productId, quantity) values (inShoppingCartId, inProductId, 1);
    end if;
     commit;
end//
delimiter ;