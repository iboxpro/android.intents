package com.example.IntegrationExample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

import java.util.Calendar;

public class MainActivity extends FragmentActivity {
    static final String TEST_PURCHASE =
            "{"+
                    "\n\t\"Title\": \"Позиция без ндс\","+
                    "\n\t\"Price\": 111.256,"+
                    "\n\t\"Quantity\": 2,"+
                    "\n\t\"TaxCode\": []"+
            "\n}";
    static final String TEST_PRODUCT = "ST00012|Product=DELIVERY|_CLIENT_NAME_=Иванов|_CONTRACT_NO_=123456";

	private FragmentTabHost mTabHost;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
                
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
		
		mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_accept)).setIndicator(getString(R.string.tab_accept)), 
				AcceptPaymentFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_reverse)).setIndicator(getString(R.string.tab_reverse)), 
				ReverseFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.tab_printer)).setIndicator(getString(R.string.tab_printer)), 
				PrinterFragment.class, null);
    }
    


}
