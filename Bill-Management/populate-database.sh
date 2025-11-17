#!/bin/bash

# Script to populate database with sample bills
# Make sure backend is running on http://localhost:7890

API_URL="http://localhost:7890/bills"

echo "=========================================="
echo "Populating Bill Management Database"
echo "=========================================="
echo ""

# Check if backend is running
echo "Checking if backend is running..."
if ! curl -s "${API_URL}/get" > /dev/null 2>&1; then
    echo "❌ Error: Backend is not running!"
    echo "Please start the backend first:"
    echo "  cd bill_management"
    echo "  mvn spring-boot:run"
    exit 1
fi
echo "✅ Backend is running"
echo ""

# Function to add a bill
add_bill() {
    local biller=$1
    local description=$2
    local amount=$3
    local mode=$4
    local status=$5
    
    echo "Adding: $biller - \$$amount"
    curl -s -X POST "${API_URL}/add" \
        -H "Content-Type: application/json" \
        -d "{
            \"biller\": \"$biller\",
            \"description\": \"$description\",
            \"amount\": \"$amount\",
            \"paymentMode\": \"$mode\",
            \"paymentStatus\": \"$status\"
        }" > /dev/null
    
    if [ $? -eq 0 ]; then
        echo "  ✅ Added successfully"
    else
        echo "  ❌ Failed to add"
    fi
}

# Add sample bills
echo "Adding sample bills..."
echo ""

add_bill "Electric Company" "Monthly electricity bill for November 2025" "150.00" "CARD" "COMPLETED"
add_bill "Water Department" "Water supply charges for November" "45.50" "UPI" "COMPLETED"
add_bill "Internet Provider" "Broadband service - 100 Mbps plan" "899.00" "NETBANKING" "COMPLETED"
add_bill "Mobile Carrier" "Postpaid mobile bill" "299.00" "UPI" "PENDING"
add_bill "Gas Company" "LPG cylinder refill" "650.00" "CASH" "PENDING"
add_bill "Netflix" "Monthly subscription - Premium plan" "649.00" "CARD" "COMPLETED"
add_bill "Amazon Prime" "Annual membership renewal" "1499.00" "CARD" "COMPLETED"
add_bill "Gym Membership" "Monthly gym membership fee" "1200.00" "UPI" "PENDING"
add_bill "House Rent" "Monthly rent for December 2025" "15000.00" "NETBANKING" "PENDING"
add_bill "Credit Card" "Credit card bill payment" "5430.00" "NETBANKING" "COMPLETED"
add_bill "Insurance Premium" "Health insurance quarterly premium" "3500.00" "NETBANKING" "PENDING"
add_bill "Grocery Store" "Monthly grocery shopping" "4500.00" "CARD" "COMPLETED"
add_bill "Electricity Board" "Electricity bill for December" "180.00" "UPI" "PENDING"
add_bill "DTH Service" "Cable TV recharge" "350.00" "UPI" "COMPLETED"
add_bill "Newspaper" "Monthly newspaper subscription" "150.00" "CASH" "COMPLETED"
add_bill "Milk Vendor" "Daily milk supply for November" "1200.00" "CASH" "COMPLETED"
add_bill "Car Loan EMI" "Monthly car loan installment" "12500.00" "NETBANKING" "COMPLETED"
add_bill "School Fees" "Tuition fees for Q4 2025" "25000.00" "NETBANKING" "PENDING"
add_bill "Medical Store" "Monthly medicines" "850.00" "CARD" "COMPLETED"
add_bill "Spotify" "Music streaming subscription" "119.00" "CARD" "COMPLETED"

echo ""
echo "=========================================="
echo "Database Population Complete!"
echo "=========================================="
echo ""

# Show summary
echo "Fetching bills summary..."
TOTAL=$(curl -s "${API_URL}/get" | grep -o "id" | wc -l)
echo "Total bills added: $TOTAL"
echo ""
echo "View all bills:"
echo "  Frontend: http://localhost (or open frontend/index.html)"
echo "  API: curl http://localhost:7890/bills/get"
echo ""
