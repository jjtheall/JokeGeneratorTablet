package edu.quinnipiac.ser210.jokegeneratortablet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JokeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JokeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String setup = "";
    private String punchline = "";

    public JokeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JokeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JokeFragment newInstance(String param1, String param2) {
        JokeFragment fragment = new JokeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_joke, container, false);

    }

    //sets setupTextView and punchLineTextView's text to setup and punchline
    //strings accordingly, as requested by async task in MainFragment
    //additionally adds onClickListener for reveal button to set punchlineTextView
    //visibility to true
    @Override
    public void onStart(){
        super.onStart();
        View view = getView();
        if(view != null){
            TextView setupTextView = view.findViewById(R.id.setup);
            TextView punchlineTextView = view.findViewById(R.id.punchline);
            setupTextView.setText(setup);
            punchlineTextView.setText(punchline);

            Button revealButton = (Button) view.findViewById(R.id.reveal);
            revealButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    punchlineTextView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void setSetup(String setup){
        this.setup = setup;
    }

    public void setPunchline(String punchline){
        this.punchline = punchline;
    }

    public String getSetup(){
        return setup;
    }

    public String getPunchline(){
        return punchline;
    }
}