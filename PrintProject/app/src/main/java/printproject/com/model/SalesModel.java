package printproject.com.model;

import printproject.com.printproject.StaticValue;

/**
 * Created by shohrab.uddin on 29.12.2015.
 */
public class SalesModel {

    String productShortName;
    int salesAmount;
    double unitSalesCost;

    public SalesModel(String productSName, int amount, double unitSCost){
        this.productShortName = productSName;
        this.salesAmount = amount;
        this.unitSalesCost = unitSCost;
    }

    public SalesModel(){

    }

    public static void generatedMoneyReceipt(){
        SalesModel salesModel = new SalesModel("Vegetable Noodle", 1, 3);
        StaticValue.arrayListSalesModel.add(salesModel);
        SalesModel salesModel1 = new SalesModel("Chicken Fry", 1, 5);
        StaticValue.arrayListSalesModel.add(salesModel1);
        SalesModel salesModel2 = new SalesModel("Coke-Small", 1, 1);
        StaticValue.arrayListSalesModel.add(salesModel2);

    }

    public String getProductShortName() {
        return productShortName;
    }

    public int getSalesAmount() {
        return salesAmount;
    }

    public double getUnitSalesCost() {
        return unitSalesCost;
    }


}
