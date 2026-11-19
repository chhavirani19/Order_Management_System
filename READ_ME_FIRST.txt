=====================================================================
          ORDER MANAGEMENT SYSTEM (Java JDBC + MySQL)
       Console menu program  +  REST API for Postman
=====================================================================

---------------------------------------------------------------------
1. PROJECT STRUCTURE (D:\project\Order_Management_System)
---------------------------------------------------------------------
src/main/java/com/example/ordermanagement/
  OrderManagementApplication.java  -> RUN THIS for REST API / Postman
  console/OrderManagementConsole.java -> RUN THIS for the menu program
                                        (answer to the question paper)
  model/Order.java                 -> MODEL (one row of Orders table)
  model/QuantityRequest.java       -> JSON body for part (c)
  model/StatusRequest.java         -> JSON body for part (d)
  dao/DBConnection.java            -> (a) MySQL connection (root/root)
  dao/OrderDao.java                -> All SQL with PreparedStatement
  service/OrderService.java        -> Rules of parts (a)-(e)
  controller/OrderController.java  -> CONTROLLER (REST endpoints)
  exception/*.java                 -> Error handling (404, 409, 400)
src/main/resources/application.properties -> port 8080
pom.xml                            -> Maven (Spring Boot + MySQL driver)
ecommerce_db.sql                   -> Creates database + table
POSTMAN_COMMANDS.txt               -> Every Postman request (open in Notepad)
Order-Management-API.postman_collection.json -> Import into Postman
AI_PROMPTS.txt                     -> 3 AI prompts for the report
Solution_Explanation.txt           -> Answer for each part + viva Q&A

---------------------------------------------------------------------
2. IMPORT INTO ECLIPSE (one time)
---------------------------------------------------------------------
  1. Open Eclipse.
  2. File -> Import -> Maven -> Existing Maven Projects -> Next
  3. Root Directory: Browse -> D:\project\Order_Management_System
     -> make sure pom.xml is ticked -> Finish
  4. Wait until "Building workspace" at the bottom right finishes.
  (If you imported the old version before: delete it from Eclipse
   first - right-click -> Delete -> do NOT tick "delete contents".)

---------------------------------------------------------------------
3. RUN THE MENU PROGRAM (question paper parts a-e)
---------------------------------------------------------------------
  src/main/java -> com.example.ordermanagement.console
  -> OrderManagementConsole.java -> Right-click
  -> Run As -> Java Application
  Type in the Console. Choose 1 first and add at least 3 orders.

    1. Insert Order Records                          (a)
    2. Display Orders by Status (quantity > 2)       (b)
    3. Increase Order Quantity                       (c)
    4. Update Order Status                           (d)
    5. Total Order Value & Delete Cancelled Order    (e)
    6. Display All Orders
    0. Exit

---------------------------------------------------------------------
4. RUN THE REST API (for Postman)
---------------------------------------------------------------------
  src/main/java -> com.example.ordermanagement
  -> OrderManagementApplication.java -> Right-click
  -> Run As -> Java Application
  Wait for "Started OrderManagementApplication" in the Console.
  Then follow POSTMAN_COMMANDS.txt.
  Stop it with the red square (Terminate) in the Console.

---------------------------------------------------------------------
5. SAMPLE DATA (menu option 1 -> enter 6)
---------------------------------------------------------------------
  ID   Customer      Product        Qty  Unit Price  Status
  101  Amit Sharma   Laptop         5    55000       Pending
  102  Neha Gupta    Mobile Phone   3    20000       Pending
  103  Ravi Kumar    Headphones     1    1500        Shipped
  105  Sunil Verma   Mouse          6    400         Pending
  106  Kavita Rao    Monitor        2    9000        Delivered
  107  Rohit Jain    Charger        3    700         Cancelled

  Both programs use the SAME table. Orders added in the console are
  seen in Postman and the other way round. The table is empty now.
  To empty it again (MySQL):  TRUNCATE ecommerce_db.Orders;

---------------------------------------------------------------------
6. SCREENSHOTS FOR THE REPORT
---------------------------------------------------------------------
  3. Model      -> open model/Order.java, screenshot the code
     Controller -> open controller/OrderController.java, screenshot
     Postman    -> run OrderManagementApplication, send each request
                   from POSTMAN_COMMANDS.txt, screenshot (URL + body +
                   response visible) - at least (a), (b), (c), (d), (e)
  4. Project Structure -> in Package Explorer expand
     src/main/java and all packages under com.example.ordermanagement,
     screenshot the left panel
  5. AI prompts -> copy from AI_PROMPTS.txt

---------------------------------------------------------------------
7. SEE DATA DIRECTLY IN MYSQL (optional)
---------------------------------------------------------------------
  "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -proot
    USE ecommerce_db;
    SELECT * FROM Orders;

---------------------------------------------------------------------
8. ON A LAB / EXAM PC
---------------------------------------------------------------------
  - Change USER and PASSWORD in dao/DBConnection.java to that PC's
    MySQL login (the database and table are created automatically).
  - Needs internet the first time so Maven can download libraries.
=====================================================================
