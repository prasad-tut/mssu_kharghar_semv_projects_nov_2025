@echo off
REM Script to populate database with sample bills
REM Make sure backend is running on http://localhost:7890

setlocal enabledelayedexpansion
set API_URL=http://localhost:7890/bills

echo ==========================================
echo Populating Bill Management Database
echo ==========================================
echo.

REM Check if backend is running
echo Checking if backend is running...
curl -s %API_URL%/get >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Backend is not running!
    echo Please start the backend first:
    echo   cd bill_management
    echo   mvn spring-boot:run
    exit /b 1
)
echo [OK] Backend is running
echo.

echo Adding sample bills...
echo.

REM Add bills
call :add_bill "Electric Company" "Monthly electricity bill for November 2025" "150.00" "CARD" "COMPLETED"
call :add_bill "Water Department" "Water supply charges for November" "45.50" "UPI" "COMPLETED"
call :add_bill "Internet Provider" "Broadband service - 100 Mbps plan" "899.00" "NETBANKING" "COMPLETED"
call :add_bill "Mobile Carrier" "Postpaid mobile bill" "299.00" "UPI" "PENDING"
call :add_bill "Gas Company" "LPG cylinder refill" "650.00" "CASH" "PENDING"
call :add_bill "Netflix" "Monthly subscription - Premium plan" "649.00" "CARD" "COMPLETED"
call :add_bill "Amazon Prime" "Annual membership renewal" "1499.00" "CARD" "COMPLETED"
call :add_bill "Gym Membership" "Monthly gym membership fee" "1200.00" "UPI" "PENDING"
call :add_bill "House Rent" "Monthly rent for December 2025" "15000.00" "NETBANKING" "PENDING"
call :add_bill "Credit Card" "Credit card bill payment" "5430.00" "NETBANKING" "COMPLETED"
call :add_bill "Insurance Premium" "Health insurance quarterly premium" "3500.00" "NETBANKING" "PENDING"
call :add_bill "Grocery Store" "Monthly grocery shopping" "4500.00" "CARD" "COMPLETED"
call :add_bill "Electricity Board" "Electricity bill for December" "180.00" "UPI" "PENDING"
call :add_bill "DTH Service" "Cable TV recharge" "350.00" "UPI" "COMPLETED"
call :add_bill "Newspaper" "Monthly newspaper subscription" "150.00" "CASH" "COMPLETED"
call :add_bill "Milk Vendor" "Daily milk supply for November" "1200.00" "CASH" "COMPLETED"
call :add_bill "Car Loan EMI" "Monthly car loan installment" "12500.00" "NETBANKING" "COMPLETED"
call :add_bill "School Fees" "Tuition fees for Q4 2025" "25000.00" "NETBANKING" "PENDING"
call :add_bill "Medical Store" "Monthly medicines" "850.00" "CARD" "COMPLETED"
call :add_bill "Spotify" "Music streaming subscription" "119.00" "CARD" "COMPLETED"

echo.
echo ==========================================
echo Database Population Complete!
echo ==========================================
echo.
echo View all bills:
echo   Frontend: http://localhost (or open frontend/index.html)
echo   API: curl http://localhost:7890/bills/get
echo.
goto :eof

:add_bill
set biller=%~1
set description=%~2
set amount=%~3
set mode=%~4
set status=%~5

echo Adding: %biller% - $%amount%
curl -s -X POST %API_URL%/add ^
    -H "Content-Type: application/json" ^
    -d "{\"biller\":\"%biller%\",\"description\":\"%description%\",\"amount\":\"%amount%\",\"paymentMode\":\"%mode%\",\"paymentStatus\":\"%status%\"}" >nul 2>&1

if errorlevel 1 (
    echo   [FAILED]
) else (
    echo   [OK]
)
goto :eof
