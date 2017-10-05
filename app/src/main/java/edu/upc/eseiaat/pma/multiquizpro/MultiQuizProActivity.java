package edu.upc.eseiaat.pma.multiquizpro;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import java.util.Locale;

public class MultiQuizProActivity extends AppCompatActivity {

    public static final String CORRECT_ANSWER = "correct_answer";
    public static final String CURRENT_QUESTION = "current_question";
    public static final String ANSWER_IS_CORRECT = "answer_is_correct";
    public static final String ANS = "ans";
    public static final String ALERT = "alert";
    private int id_answers[]= {
            R.id.answer1, R.id.answer2, R.id.answer3, R.id.answer4
    };

    private String[] all_questions;

    private TextView text_question;
    private RadioGroup group;
    private Button btn_next, btn_previous;

    private boolean[] answer_is_correct;
    private int[] ans;
    private int correct_answer;
    private int current_question;
    private boolean alert;

// Al rotar el mòbil el mètore onsaveInstance s'activa seguit d'un oncreate, el primer cop que executem
    // l'aplicació el mètode té un valor null però per mantenir alguns valors  necesitem guardarlos
    //per evitar que es recetegin al aplicar aquest mètode.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outstate.putblbla ( "correct_answer", correct_answer), fem un refactor extract constant
        //control+alt+C per a crear una constant i evitar tenir fallos al escriure al fer els gets.
        outState.putInt(CORRECT_ANSWER,correct_answer);
        outState.putInt(CURRENT_QUESTION,current_question);
        outState.putBooleanArray(ANSWER_IS_CORRECT,answer_is_correct);
        outState.putIntArray(ANS,ans);
        outState.putBoolean(ALERT,alert);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_quiz_pro);


        text_question = (TextView) findViewById(R.id.text_question);
        group = (RadioGroup) findViewById(R.id.radiogroup);
        alert = false;

        btn_next = (Button) findViewById(R.id.btn_check);
        btn_previous = (Button) findViewById(R.id.btn_previous);

        all_questions= getResources().getStringArray(R.array.all_questions);

        if(savedInstanceState == null ){
            StartOver();
        }else{
            Bundle state = savedInstanceState;
            correct_answer = state.getInt(CORRECT_ANSWER);
            current_question = state.getInt(CURRENT_QUESTION);
            answer_is_correct = state.getBooleanArray(ANSWER_IS_CORRECT);
            ans = state.getIntArray(ANS);
            //Evitar que desapareixi el quadre de diàleg al rotar el mòbil
            alert = state.getBoolean(ALERT);
            showquestion();
            if (alert) Checkresults();
            
        }



        btn_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkAnswer();

                if(current_question < all_questions.length-1){
                    current_question++;
                }else{
                    Checkresults();
                }

                showquestion();

            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkAnswer();
                if (current_question >0){
                    current_question--;
                    showquestion();
                }
            }

        });


    }

    private void StartOver() {
        alert = false;
        answer_is_correct = new boolean [all_questions.length];
        ans=new int[all_questions.length];
        for (int i=0; i< ans.length; i++){
            ans[i] = -1; //Emplenem la taula amb -1 per a indicar que l'usuari encara no ha respost
        }
        current_question=0;

        // Mètode que creem per a assignar el contingut de text de la pregunta i les respostes.
        showquestion();
    }

    private void Checkresults() {
        int corrects =0, incorrects =0, unanswered = 0;
        for(int i=0; i < all_questions.length ; i++){
            if(answer_is_correct[i]) corrects++;
            else if (ans[i]== -1) unanswered++; // ans= answered (video pauek)
            else incorrects++;
        }

        String ok = getResources().getString(R.string.ok);
        String notok = getResources().getString(R.string.notok);
        String noans = getResources().getString(R.string.noans);

        String message = String.format(Locale.getDefault(), "%s: %d\n%s: %d\n%s: %d\n", ok, corrects, notok, incorrects, noans, unanswered);
        // new alertdialog.builder és un constructor de la classe Alertdialog
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle(R.string.results);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.start_over, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StartOver();
            }
        });
        builder.create().show();
        alert = true;



    }

    private void checkAnswer() {
        int id = group.getCheckedRadioButtonId();

        int index = -1;
        for(int i=0; i< id_answers.length; i++){
            if (id_answers[i] == id){
                index = i;
            }
        }
        answer_is_correct[current_question]= (index == correct_answer);
        ans[current_question] = index;
    }

    private void showquestion() {

        String q = all_questions[current_question];
        String[] parts = q.split(";");
        group.clearCheck();
        text_question.setText(parts[0]);

        for (int i=0; i<id_answers.length; i++) {
            RadioButton rb = (RadioButton) findViewById(id_answers[i]);
            String answer = parts[i+1];
            if ( answer.charAt(0) == '*'){ // Assignem la resposta correcta a la que te l'*
                correct_answer = i ;
                answer = answer.substring(1); //Arregla que no apareixi l' *
            }
            rb.setText(answer);
            if(ans[current_question]== i){
                rb.setChecked(true);
            }
        }

        if (current_question==0){
            btn_previous.setVisibility(View.GONE);
        } else btn_previous.setVisibility(View.VISIBLE);


        if (current_question == all_questions.length-1){
            btn_next.setText(R.string.finish);
        } else btn_next.setText(R.string.next);
    }
}