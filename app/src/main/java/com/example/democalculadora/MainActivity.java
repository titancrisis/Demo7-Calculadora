package com.example.democalculadora;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.etInput)
    TextInputEditText etInput;
    @BindView(R.id.contentMain)
    RelativeLayout contentMain;

    private boolean isEditInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        configEditText();
    }

    private void configEditText() {
        etInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        etInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etInput.getRight() -
                            etInput.getCompoundDrawables()[Constantes.DRAWABLE_RIGHT].getBounds().width())) {
                        if (etInput.length() > 0) {
                            final int length = etInput.getText().length();
                            etInput.getText().delete(length - 1, length);
                        }
                    }
                }
                return false;
            }
        });
    }

    @OnClick({R.id.btn8, R.id.btn5, R.id.btn2, R.id.btn0, R.id.btn7, R.id.btn4, R.id.btn1, R.id.btnPunto, R.id.btn9, R.id.btn6, R.id.btn3})
    public void onClickNumber(View view) {
        final String valStr = ((Button) view).getText().toString();
        switch (view.getId()) {
            case R.id.btn0:
            case R.id.btn8:
            case R.id.btn5:
            case R.id.btn2:
            case R.id.btn7:
            case R.id.btn4:
            case R.id.btn1:
            case R.id.btn9:
            case R.id.btn6:
            case R.id.btn3:
                etInput.getText().append(valStr);
                break;
            case R.id.btnPunto:
                final String operacion = etInput.getText().toString();
                final String operador = Metodos.getOperator(operacion);
                final int count = operacion.length() - operacion.replace(".", "").length();
                if (!operacion.contains(Constantes.POINT) ||
                        (count < 2 && (!operador.equals(Constantes.OPERATOR_NULL)))) {
                    etInput.getText().append(valStr);
                }
                break;
        }
    }

    @OnClick({R.id.btnResult, R.id.btnClear, R.id.btnDiv, R.id.btnMulti, R.id.btnRes, R.id.btnSum})
    public void onClickControls(View view) {
        switch (view.getId()) {
            case R.id.btnSum:
            case R.id.btnRes:
            case R.id.btnMulti:
            case R.id.btnDiv:
                this.resolve(false);
                final String operador = ((Button) view).getText().toString();
                final String operacion = etInput.getText().toString();
                final String ultimoCaracter = operacion.isEmpty() ? "" : operacion.substring(operacion.length() - 1);

                if (operador.equals(Constantes.OPERATOR_SUB)) {
                    if (operacion.isEmpty() ||
                            (!(ultimoCaracter).equals(Constantes.OPERATOR_SUB)) &&
                                    !(ultimoCaracter.equals(Constantes.POINT))) {
                        etInput.getText().append(operador);
                    }
                } else {
                    if (!operacion.isEmpty() &&
                            !(ultimoCaracter.equals(Constantes.OPERATOR_SUB)) &&
                            !(ultimoCaracter.equals(Constantes.POINT))) {
                        etInput.getText().append(operador);
                    }
                }
                break;
            case R.id.btnClear:
                etInput.setText("");
                break;
            case R.id.btnResult:
                this.resolve(true);
                break;
        }
    }

    private void resolve(boolean fromResult) {
        Metodos.tryResolve(fromResult, etInput, new OnResolveCallback() {
            @Override
            public void onShowMessage(int errorRes) {
                showMessage(errorRes);
            }

            @Override
            public void onIsEditing() {
                isEditInProgress = true;
            }
        });
    }

    private void showMessage(int errorRes) {
        Snackbar.make(contentMain, errorRes, Snackbar.LENGTH_SHORT).show();
    }
}
