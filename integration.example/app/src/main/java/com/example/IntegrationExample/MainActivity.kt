package com.example.IntegrationExample

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTabHost

class MainActivity : FragmentActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        (findViewById(android.R.id.tabhost) as? FragmentTabHost)?.let {
            it.setup(this, supportFragmentManager, android.R.id.tabcontent)

            it.addTab(it.newTabSpec(getString(R.string.tab_accept)).setIndicator(getString(R.string.tab_accept)),
                    AcceptPaymentFragment::class.java, null)
            it.addTab(it.newTabSpec(getString(R.string.tab_reverse)).setIndicator(getString(R.string.tab_reverse)),
                    ReverseFragment::class.java, null)
            it.addTab(it.newTabSpec(getString(R.string.tab_printer)).setIndicator(getString(R.string.tab_printer)),
                    PrinterFragment::class.java, null)
        }
    }
}