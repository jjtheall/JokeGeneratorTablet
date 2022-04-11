package edu.quinnipiac.ser210.jokegeneratortablet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    String url1 = "https://papajoke.p.rapidapi.com/api/jokes";
    private String LOG_TAG = MainFragment.class.getSimpleName();
    Random rand = new Random();
    String setup = "";
    String punchline = "";

    public interface Listener{
        public void replaceFragment(Fragment fragment);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LaughFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    //sets onClickListener of button in view to call asyc background execution
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        Button laughButton = (Button) view.findViewById(R.id.laugh);
        laughButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchJoke().execute();
            }
        });
    }

    //method that is called after onPostExecute of async task
    //creates a new JokeFragment with setup and punchline as requested from API
    //then calls replaceFragment from interface with new fragment to replace
    public void showOtherFragment(){
        JokeFragment jokeFrag = new JokeFragment();
        jokeFrag.setSetup(setup);
        jokeFrag.setPunchline(punchline);
        Listener listener = (Listener)getActivity();
        listener.replaceFragment(jokeFrag);
    }

    class FetchJoke extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String[] jokeContents = new String[2];

            try{

                //making URL connection
                URL url = new URL(url1);
                Log.d("URL",url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-RapidAPI-Key","66653155e6mshbed1e1c6c568f68p1f931ajsn686358e46681");
                Log.d("try/catch","Reached request property");
                urlConnection.connect();
                Log.d("try/catch","Reached connect");


                InputStream in = urlConnection.getInputStream();
                if(in == null) return null;

                //creating reader
                reader = new BufferedReader(new InputStreamReader(in));
                //joke contents contains the setup and punchline as separate String objects in an array
                jokeContents = getStringArrayFromReader(reader);
                //result creates a single String that contains setup and punchline separated by "@"
                String result = jokeContents[0] + "@" + jokeContents[1];
                Log.d("result",result);
                return result;


            }catch(Exception e){
                e.printStackTrace();
            }finally{
                {
                    if(urlConnection!=null){
                        urlConnection.disconnect();
                    }
                    if(reader!=null){
                        try{
                            reader.close();
                        }catch(IOException e){
                            Log.e(LOG_TAG,"Error: " + e.getMessage());
                            return null;
                        }
                    }

                }
            }


            return null;
        }


        //TODO: (I THINK) replace the intent in onPostExecute to create a new instance
        //TODO: of JokeFragment that passes the two extras as instance vars
        @Override
        protected void onPostExecute(String result){
            if(result!=null){
                //finds index of "@" in result string, and parses through to separate setup and punchline
                //creates intent with two String extras for setup and punchline and starts JokeActivity
                int atIndex = result.indexOf('@');
                setup = result.substring(0,atIndex);
                punchline = result.substring(atIndex+1);
                Log.d("setup after onPostExecute", setup);
                Log.d("punchline after onPostExecute", punchline);
                showOtherFragment();
            }
            else{
                Log.d("Results", "results were null");
            }
        }
    }

    private String[] getStringArrayFromReader(BufferedReader reader){
        StringBuffer buffer = new StringBuffer();
        String line;
        String[] results = new String[2];

        if(reader != null){
            try{
                while((line = reader.readLine()) != null){
                    buffer.append(line + '\n');
                }
                reader.close();

                //parsing through JSON data
                //API returns an array of jokes, so we create a JSONArray holding the jokes
                JSONObject jokeContentsJSONObj = new JSONObject(buffer.toString());
                Log.d("jokeContentsJSONObj",jokeContentsJSONObj.toString());
                JSONArray jokes = (JSONArray)jokeContentsJSONObj.get("items");

                int jokeNum = 0;
                JSONObject joke = null;

                //we only want two-parter jokes, so we keep pulling jokes from the JSONArray until
                //we get a non one-liner
                do{
                    jokeNum = rand.nextInt(26);
                    joke = jokes.getJSONObject(jokeNum);
                }while(((String)joke.get("type")).equals("oneliner"));

                //getting setup and punchline strings from JSONObject
                String setup = (String)joke.get("headline");
                String punchline = (String)joke.get("punchline");
                Log.d("setup", setup);
                Log.d("punchline",punchline);
                results[0] = setup;
                results[1] = punchline;
                return results;

            }catch (Exception e){
                Log.e("CategorySelectorActivity","Error: " + e.getMessage());
                return null;
            }
            finally{

            }
        }
        return null;
    }

}