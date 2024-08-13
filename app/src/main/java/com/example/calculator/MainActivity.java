package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.inputs);
        setButtonListeners();
    }

    private void setButtonListeners() {
        int[] buttonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3,
                R.id.button4, R.id.button5, R.id.button6, R.id.button7,
                R.id.button8, R.id.button9, R.id.buttonDubleZero, R.id.buttonPeriod,
                R.id.buttonPlus, R.id.buttonSub, R.id.buttonDiv, R.id.buttonEqual
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(buttonClickListener);
        }
    }

    private final View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            String tag = (String) button.getTag();
            String text = button.getText().toString();

            switch (tag) {
                case "number":
                case "operator":
                    inputText.append(text);
                    break;

                case "clear":
                    inputText.setText("");
                    break;

                case "equal":
                    String expression = inputText.getText().toString();
                    try {
                        String postfix = infixToPostfix(expression);
                        double result = evaluatePostfix(postfix);
                        inputText.setText(String.valueOf(result));
                    } catch (Exception e) {
                        inputText.setText("0");
                        Toast.makeText(MainActivity.this, "Error in expression", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private String infixToPostfix(String expression) {
        StringBuilder result = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                result.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                result.append(' ');
                while (!stack.isEmpty() && stack.peek() != '(') {
                    result.append(stack.pop()).append(' ');
                }
                stack.pop();
            } else if (isOperator(c)) {
                result.append(' ');
                while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek())) {
                    result.append(stack.pop()).append(' ');
                }
                stack.push(c);
            }
        }
        while (!stack.isEmpty()) {
            result.append(' ').append(stack.pop());
        }
        return result.toString();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    private int precedence(char c) {
        switch (c) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
            case '%':
                return 2;
            default:
                return -1;
        }
    }

    private double evaluatePostfix(String postfix) {
        Stack<Double> stack = new Stack<>();
        for (String token : postfix.split("\\s+")) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token.charAt(0))) {
                double secondValue = stack.pop();
                double firstValue = stack.pop();
                double result = calculateResult(firstValue, secondValue, token.charAt(0));
                stack.push(result);
            }
        }
        return stack.pop();
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private double calculateResult(double firstValue, double secondValue, char operator) {
        switch (operator) {
            case '+':
                return firstValue + secondValue;
            case '-':
                return firstValue - secondValue;
            case '/':
                if (secondValue == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return firstValue / secondValue;
            case '*':
                return firstValue * secondValue;
            case '%':
                return firstValue % secondValue;
            default:
                return 0;
        }
    }
}
