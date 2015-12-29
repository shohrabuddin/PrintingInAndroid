package printproject.com.printproject;

import android.app.Dialog;

import java.util.ArrayList;

import printproject.com.model.SalesModel;

/**
 * Created by shohrab.uddin on 29.12.2015.
 */
public class StaticValue {
    public static boolean  isPrinterConnected=false;
    public static ArrayList<SalesModel> arrayListSalesModel = new ArrayList<SalesModel>();
    public static final String CURRENCY = "EUR";
    public static final double VAT = 10.00;
    public static final String VAT_REGISTRATION_NUMBER ="8877BD9877";
    public static final String BRANCH_ADDRESS ="70188, Stuttgart, Germany";
}
