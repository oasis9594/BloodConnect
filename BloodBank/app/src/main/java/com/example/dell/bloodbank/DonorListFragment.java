package com.example.dell.bloodbank;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DonorListFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";


    String type;
    RecyclerView recyclerView;
    DonorCustomAdapter donorCustomAdapter;
    ArrayList<MyDonors> donorList = new ArrayList<>();

    public DonorListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DonorListFragment newInstance(int sectionNumber) {
        DonorListFragment fragment = new DonorListFragment();
        Bundle args = new Bundle();
        switch (sectionNumber) {
            case 1:
                args.putString(ARG_SECTION_NUMBER, "A");
                break;
            case 2:
                args.putString(ARG_SECTION_NUMBER, "B");
                break;
            case 3:
                args.putString(ARG_SECTION_NUMBER, "AB");
                break;
            case 4:
                args.putString(ARG_SECTION_NUMBER, "O");
                break;
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_tab, container, false);
        type = getArguments().getString(ARG_SECTION_NUMBER);
        Log.v(getTAG(), "onCreateView " + type);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Blood Donors");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.DonorList);
        recyclerView.setLayoutManager(layoutManager);

        getData();

        donorCustomAdapter = new DonorCustomAdapter(donorList, getActivity());

        recyclerView.setAdapter(donorCustomAdapter);
        return rootView;
    }

    public void getData() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            FetchDonorsTask donorsTask = new FetchDonorsTask();
            donorsTask.execute();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection!!!!", Toast.LENGTH_SHORT).show();
        }
        FetchDonorsTask donorsTask = new FetchDonorsTask();
        donorsTask.execute();
    }

    private String getTAG() {
        return MyUtils.TAG;
    }

    ArrayList<MyDonors> getDonorDataFromJson(String donorJsonStr) throws JSONException {
        ArrayList<MyDonors> donors = new ArrayList<>();
        final String DONOR_ID = "id";
        final String DONOR_NAME = "name";
        final String DONOR_EMAIL = "email";
        final String DONOR_AGE = "age";
        final String DONOR_GENDER = "gender";
        final String DONOR_CONTACT = "contact";
        final String DONOR_CITY = "city";
        final String DONOR_COUNTRY = "country";
        final String DONOR_TYPE = "type";
        final String DONOR_SIGN = "sign";
        JSONArray donorArray = new JSONArray(donorJsonStr);
        for (int i = 0; i < donorArray.length(); i++) {
            JSONObject obj = donorArray.getJSONObject(i);
            MyDonors donor = new MyDonors();
            donor.setId(obj.getInt(DONOR_ID));
            donor.setName(obj.getString(DONOR_NAME));
            donor.setEmail(obj.getString(DONOR_EMAIL));
            donor.setAge(obj.getInt(DONOR_AGE));
            donor.setGender(obj.getString(DONOR_GENDER));
            donor.setContact(obj.getString(DONOR_CONTACT));
            donor.setCity(obj.getString(DONOR_CITY));
            donor.setCountry(obj.getString(DONOR_COUNTRY));
            donor.setBloodGroup(obj.getString(DONOR_TYPE));
            donor.setSign(obj.getString(DONOR_SIGN).equals("-"));
            donors.add(donor);
        }
        return donors;
    }

    public class FetchDonorsTask extends AsyncTask<Void, Void, ArrayList<MyDonors>> {

        @Override
        protected void onPostExecute(ArrayList<MyDonors> donors) {
            if (donors != null) {
                donorList.clear();
                for (MyDonors o : donors) {
                    Log.v(getTAG(), o.getBloodGroup());
                    if (o.getBloodGroup().equals(type))
                        donorList.add(o);
                    Log.v(getTAG(), o.getName());
                }
                donorCustomAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected ArrayList<MyDonors> doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String donorJsonString = null;
            try {
                String u = MyUtils.BASE_URL + "appdata_sender.php";

                URL url = new URL(u);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.v(getTAG(), "inputstream null");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    Log.v(MyUtils.TAG, "Stream empty while fetching data");
                    return null;
                }
                donorJsonString = buffer.toString();
                Log.v(getTAG(), donorJsonString);

            } catch (Exception e) {
                Log.e(getTAG(), "Error getting data!!!!!: " + e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(getTAG(), "Error closing stream", e);
                    }
                }
            }
            try {
                return getDonorDataFromJson(donorJsonString);
            } catch (JSONException e) {
                Log.e(getTAG(), "Error parsing JSON: " + e.getMessage());
            }
            return null;
        }
    }
}
