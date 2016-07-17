package com.example.dell.bloodbank;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;


public class RegisterDonor extends Fragment {


    //Enter url here
    private static final String REGISTER_URL = "http://[ipv4-address]:[port-number]/BloodConnect/blood_bank/appdata_reciever.php";
    EditText eName, eAge, eEmail, eCountry, eCity, eContact ;
    Spinner eType, eGender, eExp;
    Button register;
    String name, email, country, city, contact, type, sign, s, gender, exp, age;
    public RegisterDonor() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register_donor, container, false);

        eName = (EditText)v.findViewById(R.id.register_name);
        eAge=(EditText)v.findViewById(R.id.register_age);
        eEmail=(EditText)v.findViewById(R.id.register_email);
        eCountry=(EditText)v.findViewById(R.id.register_country);
        eCity=(EditText)v.findViewById(R.id.register_city);
        eContact=(EditText)v.findViewById(R.id.register_contact);
        eGender=(Spinner)v.findViewById(R.id.spinner_gender);
        eType=(Spinner)v.findViewById(R.id.spinner_type);
        eExp=(Spinner)v.findViewById(R.id.spinner_exp);
        register=(Button)v.findViewById(R.id.button_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return v;
    }

    private void registerUser()
    {
        name=eName.getText().toString();
        email=eEmail.getText().toString();
        country=eCountry.getText().toString();
        city=eCity.getText().toString();
        contact=eContact.getText().toString();
        age=eAge.getText().toString();
        s=eType.getSelectedItem().toString();
        type=s.substring(0, s.length()-1);
        sign=s.substring(s.length()-1);
        gender=eGender.getSelectedItem().toString();
        exp=eExp.getSelectedItem().toString();

        if(name.equals("")||email.equals("")||contact.equals("")||country.equals("")||city.equals("")||age.equals(""))
        {
            Toast.makeText(getActivity(), "Enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (exp)
        {
            case "1 month":
                exp="1";
                break;
            case "6 months":
                exp="6";
                break;
            case "12 months":
                exp="12";
                break;
        }
        try {
            final int a = Integer.parseInt(age);
            if(a<11||a>59) {
                Toast.makeText(getActivity(), "Enter valid age", Toast.LENGTH_SHORT).show();
                return ;
            }
        }catch (NumberFormatException e)
        {
            Toast.makeText(getActivity(), "Enter valid age", Toast.LENGTH_SHORT).show();
            return ;
        }

        //Print Log statements to check data
        Log.v(MyUtils.TAG, name+ " " + age + " " + gender + " " + exp + " "+ type + " " + sign);

        class RegisterAsyncTask extends AsyncTask<Void, Void, String> {

            RegisterUserClass ruc = new RegisterUserClass();

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> data = new HashMap<>();
                data.put("iname", name);
                data.put("icontact", contact);
                data.put("icountry", country);
                data.put("icity", city);
                data.put("iemail", email);
                data.put("igender", gender);
                data.put("iage", age);
                data.put("itype", type);
                if(sign == "+")
                    data.put("isign", "1");
                else
                    data.put("isign","0");
                data.put("iexpiration", exp);
                data.put("isubmit", "1");

                return ruc.sendPostRequest(REGISTER_URL, data);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getActivity().getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }
        }
        RegisterAsyncTask ru=new RegisterAsyncTask();
        ru.execute();
    }
}
