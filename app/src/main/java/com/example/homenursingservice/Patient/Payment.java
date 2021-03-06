package com.example.homenursingservice.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.example.homenursingservice.CustomAlertDialog;
import com.example.homenursingservice.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sslcommerz.library.payment.model.datafield.MandatoryFieldModel;
import com.sslcommerz.library.payment.model.dataset.TransactionInfo;
import com.sslcommerz.library.payment.model.util.CurrencyType;
import com.sslcommerz.library.payment.model.util.ErrorKeys;
import com.sslcommerz.library.payment.model.util.SdkCategory;
import com.sslcommerz.library.payment.model.util.SdkType;
import com.sslcommerz.library.payment.viewmodel.listener.OnPaymentResultListener;
import com.sslcommerz.library.payment.viewmodel.management.PayUsingSSLCommerz;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Random;

public class Payment {
    String store_id="";
    String store_pass="";
    String amount="";
    String document_id="";
    Context context;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    public Payment(Context context){
        this.context=context;
    }
    public void sendPayment(final String amount, String document_id,final boolean exit) {
        this.document_id=document_id;
        this.amount=amount;
        store_id="giftk5fe3fc5b0ba0d";
        store_pass="giftk5fe3fc5b0ba0d@ssl";
        byte[] array = new byte[15]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));
        String transaction_id=generatedString;
        MandatoryFieldModel mandatoryFieldModel = new MandatoryFieldModel(store_id, store_pass, amount, transaction_id, CurrencyType.BDT, SdkType.TESTBOX, SdkCategory.BANK_LIST);

        PayUsingSSLCommerz.getInstance().setData(context, mandatoryFieldModel, new OnPaymentResultListener() {
            @Override
            public void transactionSuccess(TransactionInfo transactionInfo) {
                HashMap<String,String> map=new HashMap<>();
                map.put("result","fail");
                if (transactionInfo.getRiskLevel().equals("0")) {
                    map.put("result","success");
                    map.put("transaction_id",transactionInfo.getTranId());
                    map.put("valid_id",transactionInfo.getValId());
                    map.put("status",transactionInfo.getStatus());
                    map.put("session_key",transactionInfo.getSessionkey());
                    map.put("store_id",store_id);
                    map.put("store_pass",store_pass);
                    map.put("value_a",transactionInfo.getValueA());
                    map.put("value_b",transactionInfo.getValueB());
                    map.put("value_c",transactionInfo.getValueC());
                    map.put("value_d",transactionInfo.getValueD());
                    map.put("amount",transactionInfo.getAmount());
                    map.put("api_connect",transactionInfo.getAPIConnect());
                    map.put("bank_tran_id",transactionInfo.getBankTranId());
                    map.put("base_fair",transactionInfo.getBaseFair());
                    map.put("card_brand",transactionInfo.getCardBrand());
                    map.put("card_issuer",transactionInfo.getCardIssuer());
                    map.put("card_issuer_country",transactionInfo.getCardIssuerCountry());
                    map.put("card_issuer_country_code",transactionInfo.getCardIssuerCountryCode());
                    map.put("card_no",transactionInfo.getCardNo());
                    map.put("card_type",transactionInfo.getCardType());
                    map.put("currency_amount",transactionInfo.getCurrencyAmount());
                    map.put("currency_rate",transactionInfo.getCurrencyRate());
                    map.put("currency_type",transactionInfo.getCurrencyType());
                    map.put("gw_version",transactionInfo.getGwVersion());
                    map.put("risk_level",transactionInfo.getRiskLevel());
                    map.put("risk_title",transactionInfo.getRiskTitle());
                    map.put("store_amount",transactionInfo.getStoreAmount());
                    map.put("tran_date",transactionInfo.getTranDate());
                    map.put("validated_on",transactionInfo.getValidatedOn());
                    updatePayment(transactionInfo.getTranId(),amount,transactionInfo.getStoreAmount());
                    CustomAlertDialog.getInstance().success_message(context,"Home Nursing Service","Payment Successful",exit);

                }

                else {
                    CustomAlertDialog.getInstance().success_message(context,"Home Nursing Service","Payment Fail.Try Again Later",exit);
                    Toast.makeText(context,"Transaction in risk. Risk Title : " + transactionInfo.getRiskTitle(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void transactionFail(String s) {
                CustomAlertDialog.getInstance().success_message(context,"Home Nursing Service","Payment Fail.Try Again Later",exit);
                Toast.makeText(context,"Transaction Failed."+s,Toast.LENGTH_LONG).show();
            }


            @Override
            public void error(int errorCode) {
                switch (errorCode) {
                    case ErrorKeys.USER_INPUT_ERROR:
                        Toast.makeText(context,"User Input Error",Toast.LENGTH_LONG).show();
                        break;
                    case ErrorKeys.INTERNET_CONNECTION_ERROR:
                        Toast.makeText(context,"INTERNET_CONNECTION_ERROR",Toast.LENGTH_LONG).show();
                        break;
                    case ErrorKeys.DATA_PARSING_ERROR:
                        Toast.makeText(context,"DATA_PARSING_ERROR",Toast.LENGTH_LONG).show();
                        break;
                    case ErrorKeys.CANCEL_TRANSACTION_ERROR:
                        Toast.makeText(context,"CANCEL_TRANSACTION_ERROR",Toast.LENGTH_LONG).show();
                        break;
                    case ErrorKeys.SERVER_ERROR:
                        Toast.makeText(context,"SERVER_ERROR",Toast.LENGTH_LONG).show();
                        break;
                    case ErrorKeys.NETWORK_ERROR:
                        Toast.makeText(context,"NETWORK_ERROR",Toast.LENGTH_LONG).show();
                        break;
                }
                CustomAlertDialog.getInstance().success_message(context,"Home Nursing Service","Payment Fail.Try Again Later",exit);
            }
        });
    }
    public void updatePayment(String tranId,String amount,String net_amount)
    {
        double paymentAmount=Double.parseDouble(amount);
        double netPaymentAmount=Double.parseDouble(net_amount);
        double charge=paymentAmount-netPaymentAmount;
        HashMap<String,Object> data=new HashMap<>();
        data.put("payment",paymentAmount);
        data.put("transaction_id",tranId);
        data.put("net_amount",netPaymentAmount);
        data.put("transaction_charge",charge);
        db.collection("AllServices").document(document_id).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

}