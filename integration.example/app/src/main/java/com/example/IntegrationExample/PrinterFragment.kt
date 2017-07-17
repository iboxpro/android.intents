package com.example.IntegrationExample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class PrinterFragment : Fragment() {

    lateinit var edtIntroduce: EditText
    lateinit var btnOpenShift: Button
    lateinit var btnCloseShift: Button
    lateinit var txtResult: EditText

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_printer, container, false)

        // edtIntroduce = (EditText)view.findViewById(R.id.edtIntroduce); TODO
        btnOpenShift = view.findViewById(R.id.btnOpenShift) as Button
        btnCloseShift = view.findViewById(R.id.btnCloseShift) as Button
        txtResult = view.findViewById(R.id.txtResult) as EditText

        btnOpenShift.setOnClickListener { openShift() }

        btnCloseShift.setOnClickListener { closeShift() }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 503) {
            if (resultCode == Activity.RESULT_OK) {
                txtResult.setText("Shift was opened")
            } else
                txtResult.setText("Cant open shift")
        }

        if (requestCode == 504) {
            if (resultCode == Activity.RESULT_OK) {
                if (data?.extras?.containsKey("Registers") ?: false)
                    txtResult.setText("Registers :\n" + data!!.extras.getString("Registers")!!)
            } else
                txtResult.setText("Cant close shift")
        }
    }

    private fun openShift() {
        val intent = Intent("ru.ibox.pro.printer").apply {
            putExtra("Email", getString(R.string.login))
            putExtra("Password", getString(R.string.password))
            putExtra("Action", "OpenShift")
        }

        try {
            val introduce = edtIntroduce.text.toString().toDouble()
            if (introduce > 0.0) {
                intent.putExtra("Cash", introduce)
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

        startActivityForResult(intent, 503)
    }

    private fun closeShift() {
        val intent = Intent("ru.ibox.pro.printer").apply {
            putExtra("Email", getString(R.string.login))
            putExtra("Password", getString(R.string.password))
            putExtra("Action", "CloseShift")
        }
        startActivityForResult(intent, 504)
    }
}