package id.kasandra.retail;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
	// LogCat tag
	private static String TAG = SessionManager.class.getSimpleName();

	// Shared Preferences
	SharedPreferences pref;

	Editor editor;
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Shared preferences file name
	private static final String PREF_NAME = "kasandra";
	
	private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
	private static final String KEY_IS_LOGGED_IN_CLIENT = "isLoggedInClient";

	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setDate(String sDate) {
		editor.putString("date", sDate);
		editor.commit();

		//Log.d(TAG, "Date modified!");
	}

	public String sDate(){
		return pref.getString("date", "Date");
	}

	public void setWiFi(String WiFi) {
		editor.putString("wifi", WiFi);
		// commit changes
		editor.commit();

		//Log.d(TAG, "WiFi modified!");
	}

    public void setWiFiInfo(String WiFiUser, String WiFiPasswd) {
        editor.putString("wifiuser", WiFiUser);
        editor.putString("wifipass", WiFiPasswd);
        // commit changes
        editor.commit();

        //Log.d(TAG, "WiFiInfo modified!");
    }

    public void setURL(String URL) {
        editor.putString("url", URL);
        editor.commit();

        //Log.d(TAG, "URL modified!");
    }

	public void setSavedTrue(boolean isSaved, int customerid) {
		editor.putBoolean("is_saved", isSaved);
		editor.putInt("customerid", customerid);
		editor.commit();

		//Log.d(TAG, "saved true modified!");
	}

	public void setIsAdmin(boolean isAdmin) {
		editor.putBoolean("is_admin", isAdmin);
		editor.commit();

		//Log.d(TAG, "client admin modified!");
	}

	public String sWiFi(){
		return pref.getString("wifi", "WiFi disconnected");
	}

    public String sURL(){
        return pref.getString("url", "kasandra.biz");
    }

    public String sWiFiUser(){
        return pref.getString("wifiuser", "Username");
    }

    public String sWiFiPasswd(){
        return pref.getString("wifipass", "Password");
    }

	public void setLogin(boolean isLoggedIn) {
		editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
		editor.commit();

		//Log.d(TAG, "User login session modified!");
	}

	public void setLoginClient(boolean isLoggedInClient,  int client_id) {
		editor.putBoolean(KEY_IS_LOGGED_IN_CLIENT, isLoggedInClient);
		editor.putInt("client_id", client_id);
		editor.commit();
		//Log.d(TAG, "Client login session modified!");
	}

	public void setOutlet(int outlet_id,  String outlet) {
		editor.putInt("outlet_id", outlet_id);
		editor.putString("outlet", outlet);
		editor.commit();
		//Log.d(TAG, "Outlet session modified!");
	}

	public void setToken(String token) {
		editor.putString("token", token);
		editor.commit();
		//Log.d(TAG, "Token modified!");
	}

	public void setIMEI(String IMEI) {
		editor.putString("imei", IMEI);
		editor.commit();
		//Log.d(TAG, "IMEI modified!");
	}

    public void setVoucher(String voucher) {
        editor.putString("voucher", voucher);
        editor.commit();
        //Log.d(TAG, "Voucher modified!");
    }

	public void setPrinterChars(int printerchars) {
		editor.putInt("printerchars", printerchars);
		editor.commit();
		//Log.d(TAG, "Voucher modified!");
	}

	public void setClientName(String sClientName) {
		editor.putString("client_name", sClientName);
		editor.commit();

		//Log.d(TAG, "Client name modified!");
	}

	public void setOperName(String sOperName) {
		editor.putString("operator_name", sOperName);
		editor.commit();

		//Log.d(TAG, "operator name modified!");
	}

	public void setUserEmail(String sUserEmail) {
		editor.putString("user_email", sUserEmail);
		editor.commit();

		//Log.d(TAG, "user email modified!");
	}

	public void setBTAddress(String sBTAddress) {
		editor.putString("bt_address", sBTAddress);
		editor.commit();

		//Log.d(TAG, "bluetooth address modified!");
	}

	public void setUserID(int nUserID) {
		editor.putInt("user_id", nUserID);
		editor.commit();

		//Log.d(TAG, "user ID modified!");
	}

	public void setUserName(String sUserName) {
		editor.putString("user_name", sUserName);
		editor.commit();

		//Log.d(TAG, "user name modified!");
	}

	public void setClientFullName(String sClientFullName) {
		editor.putString("client_full_name", sClientFullName);
		editor.commit();

		//Log.d(TAG, "client full name modified!");
	}

	public void setFullName(String sFullName) {
		editor.putString("full_name", sFullName);
		editor.commit();

		//Log.d(TAG, "full name modified!");
	}

	public void setPasswd(String sPasswd) {
		editor.putString("user_pass", sPasswd);
		editor.commit();

		//Log.d(TAG, "user name modified!");
	}

	public void setClientPasswd(String sClientPasswd) {
		editor.putString("client_passwd", sClientPasswd);
		editor.commit();

		//Log.d(TAG, "Client passwd modified!");
	}

	public void setClientRegDate(String sClientRegDate) {
		editor.putString("client_regdate", sClientRegDate);
		editor.commit();

		//Log.d(TAG, "client regdate modified!");
	}

	public void setClientAddr(String sClientAddr) {
		editor.putString("client_addr", sClientAddr);
		editor.commit();

		//Log.d(TAG, "Client addr modified!");
	}

	public void setDataRetention(String sDataRetention) {
		editor.putString("data_retention", sDataRetention);
		editor.commit();

		//Log.d(TAG, "data retention modified!");
	}

	public void setCatID(int nCatID) {
		editor.putInt("cat_id", nCatID);
		editor.commit();

		//Log.d(TAG, "Category ID modified!");
	}

	public int nCatID(){
		return pref.getInt("cat_id", -888);
	}

	public void setCustID(int nCustID) {
		editor.putInt("cust_id", nCustID);
		editor.commit();
	}

	public int nCustID(){
		return pref.getInt("cust_id", -777);
	}

	public void setTransNO(int nTransNO) {
		editor.putInt("trans_no", nTransNO);
		editor.commit();

		//Log.d(TAG, "Transaction NO modified!");
	}

	public void setTransID(int nTransID) {
		editor.putInt("trans_id", nTransID);
		editor.commit();

		//Log.d(TAG, "Transaction ID modified!");
	}

	public void setTransRealNo(int nTransRealNo) {
		editor.putInt("trans_realno", nTransRealNo);
		editor.commit();

		//Log.d(TAG, "Transaction NO modified!");
	}

	public int nTransID(){
		return pref.getInt("trans_id", -0);
	}

	public int nTransNO(){
		return pref.getInt("trans_no", -0);
	}

	public int nTransRealNo(){
		return pref.getInt("trans_realno", -0);
	}

	public void setSubTotal(double nSubTotal) {
		editor.putLong("subtotal", Double.doubleToLongBits(nSubTotal));
		editor.commit();

		//Log.d(TAG, "Subtotal modified!");
	}

	public double nSubTotal(){
		return Double.longBitsToDouble(pref.getLong("subtotal", 0));
	}

	public void setDiscTotal(double nDiscTotal) {
		editor.putLong("disctotal", Double.doubleToLongBits(nDiscTotal));
		editor.commit();

		//Log.d(TAG, "Disctotal modified!");
	}

	public double nDiscTotal(){
		return Double.longBitsToDouble(pref.getLong("disctotal", 0));
	}

	public void setTaxValue(int nTaxValue) {
		editor.putInt("tax_value", nTaxValue);
		editor.commit();

		//Log.d(TAG, "Tax Value modified!");
	}

	public int nTaxValue(){
		return pref.getInt("tax_value", 10);
	}

	public void setSCValue(int nSCValue) {
		editor.putInt("service_charge", nSCValue);
		editor.commit();

		//Log.d(TAG, "Tax Value modified!");
	}

	public int nSCValue(){
		return pref.getInt("service_charge", 0);
	}

	public boolean isLoggedIn(){
		return pref.getBoolean(KEY_IS_LOGGED_IN, false);
	}

	public boolean isLoggedInClient(){
		return pref.getBoolean(KEY_IS_LOGGED_IN_CLIENT, false);
	}

	public boolean isAdmin(){
		return pref.getBoolean("is_admin", false);
	}

	public boolean isSaved(){
		return pref.getBoolean("is_saved", true);
	}

	public int nCustomerId(){
		return pref.getInt("customerid", -100);
	}

	public int nClient_ID(){
		return pref.getInt("client_id", -999);
	}

	public int nOutlet_ID(){
		return pref.getInt("outlet_id", -800);
	}

	public String sOutlet(){
		return pref.getString("outlet", "");
	}

	public String sToken(){
		return pref.getString("token", "Token belum didefinisikan");
	}

	public String sIMEI(){
		return pref.getString("imei", "IMEI not Found");
	}

    public String sVoucher(){
        return pref.getString("voucher", "58");
    }

	public int nPrinterChars(){
		return pref.getInt("printerchars", 42);
	}

	public String sClientRegDate() { return pref.getString("client_regdate", "Registration Date"); }

	public String sClientAddr() { return pref.getString("client_addr", "Address not Found"); }

	public String sOperName(){
		return pref.getString("operator_name", "Operator");
	}

	public String sClientName() { return pref.getString("client_name", ""); }

	public void setOperId(int nOperIdPersistence) {
		editor.putInt("id_operator", nOperIdPersistence);
		editor.commit();
		Log.d(TAG, "Operator Id stored!");
	}

	public void setOperId_(int nOperId_) {
		editor.putInt("oper_id_", nOperId_);
		editor.commit();
		Log.d(TAG, "Operator Id stored!");
	}

	public String sUserName(){
		return pref.getString("user_name", "Username");
	}

	public String sClientFullName(){
		return pref.getString("client_full_name", "Fullname");
	}

	public String sFullName(){
		return pref.getString("full_name", "Fullname");
	}

	public String sPasswd(){
		return pref.getString("user_pass", "Password");
	}

	public String sClientPasswd() { return pref.getString("client_passwd", "Password"); }

	public String sUserEmail(){
		return pref.getString("user_email", "Email");
	}

	public String sBTAddress(){
		return pref.getString("bt_address", null);
	}

	public int nUserID(){
		return pref.getInt("user_id", -900);
	}

	public int nOperIdPersistence(){
		return pref.getInt("id_operator", -99);
	}

	public int nOperId_(){
		return pref.getInt("oper_id_", -999);
	}

	public String sDataRetention(){
		return pref.getString("data_retention", "-7 days");
	}

	public void setScreenSize(double screenSize) {

		editor.putLong("screen", Double.doubleToLongBits(screenSize));
		editor.commit();

		//Log.d(TAG, "Screen size session modified!");
	}

	public double screenSize(){
		return Double.longBitsToDouble(pref.getLong("screen", 8));
	}

	public String address() {
		return pref.getString("address", "Address not found");
	}

	public String message() {
		return pref.getString("message", "");
	}

	public void setLatLng(double latitude, double longitude) {

		editor.putLong("latitude", Double.doubleToLongBits(latitude));
		editor.putLong("longitude", Double.doubleToLongBits(longitude));
		// commit changes
		editor.commit();

		Log.d(TAG, "Store last position");
	}

	public double longitude(){
		return Double.longBitsToDouble(pref.getLong("longitude", (long) 0.0));
	}

	public double latitude(){
		return Double.longBitsToDouble(pref.getLong("latitude", (long) 0.0));
	}

	public boolean getShowcase(){
		return pref.getBoolean("showcase", false);
	}

	public void setShowcase(boolean shown) {
		editor.putBoolean("showcase", shown);
		editor.commit();
	}

	public boolean isInternetAvailable(){
		return pref.getBoolean("online", false);
	}

	public void setInternetAvailable(boolean isOnline) {
		editor.putBoolean("online", isOnline);
		editor.commit();
	}

	public String sJWT(){
		return pref.getString("jwt", "");
	}

	public void setJWT(String jwt) {
		editor.putString("jwt", jwt);
		editor.commit();
	}

	public boolean getDoubleReceipt(){
		return pref.getBoolean("receipt", false);
	}

	public void setDoubleReceipt(boolean doubleReceipt) {
		editor.putBoolean("receipt", doubleReceipt);
		editor.commit();
	}

	public void clearAllData() {
		editor.clear();
		editor.commit();
	}
}
