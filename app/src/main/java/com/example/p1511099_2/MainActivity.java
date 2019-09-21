package com.example.p1511099_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public String expression = "";

    public int add(int a, int b){

        a = ternaryToDecimal(a);
        b = ternaryToDecimal(b);

        int result = a + b;

        return decimalToTernary(result);
    }

    public int multiply(int a, int b){

        a = ternaryToDecimal(a);
        b = ternaryToDecimal(b);

        int result = a * b;

        return decimalToTernary(result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void addChar(View view)
    {

        TextView input = findViewById(R.id.input);

        switch(view.getId()){
            case R.id.zero:
                expression += '0';
                break;

            case R.id.one:
                expression += '1';
                break;

            case R.id.two:
                expression += '2';
                break;

            case R.id.add:
                expression += '+';
                break;

            case R.id.multiply:
                expression += '*';
                break;
        }

        input.setText(expression);

    }

    public void showResult(View view)
    {

        TextView output = findViewById(R.id.output);
        String result = ternaryToDecimal(parser()) + "";
        output.setText(result);

    }

    public void resetExp(View view)
    {

        TextView input = findViewById(R.id.input);
        TextView output = findViewById(R.id.output);
        expression = "";
        input.setText(expression);
        output.setText("");

    }

    public int ternaryToDecimal(int num) {
        String expression = Integer.toString(num);
        int cursor = 0;
        int decimal = 0;

        for (int i = expression.length() - 1; i >= 0; i--)
        {
            decimal += Integer.parseInt(expression.charAt(i) + "") * Math.pow(3, cursor);
            cursor++;
        }

        return decimal;
    }

    public int decimalToTernary(int num) {

        String exp = "";
        String reversed = "";

        while(num > 0)
        {
            exp += num % 3;
            num = num / 3;
        }

        for (int i = exp.length() - 1; i >= 0; i--) {
            reversed += exp.charAt(i);
        }

        return Integer.parseInt(reversed);
    }

    public int parser()
    {

        // 예외 처리
        exceptionHandler();

        // 만약 빈 문자열이 넘어왔다면, 0을 리턴
        if(expression.equals(""))
        {
            return 0;
        }

        // 만약 연산자가 들어있지 않다면, 연산자가 들어있지 않은 정수 그대로를 리턴
        else if(!expression.contains("*") && !expression.contains("+"))
        {
            return Integer.parseInt(expression);
        }

        // 곱셈 연산을 먼저 진행
        String[] splitExp = expression.split("(?<=[+])|(?=[+])"); // 정규표현식의 전후방탐색을 활용하여 +,- 연산자를 구분자로 분할
        expression = "";
        for (int i = 0; i < splitExp.length; i++) {
            if (splitExp[i].contains("*")) { // 나눠진 배열들 중 *,/ 연산자를 포함하는 배열의 경우

                // 문자열 처리를 위한 StringBuilder 클래스 선언
                StringBuilder splitStringBuilder = new StringBuilder(splitExp[i]);

                // 해당 문자열이 * 혹은 / 연산자를 가지고 있지 않을 때까지 반복
                while (splitExp[i].contains("*")) {

                    String a = ""; // 피연산자 a
                    String b = ""; // 피연산자 b
                    char operator; // 연산자
                    int index = 0; // 문자열을 구성하는 char의 인덱스

                    // 1. * 연산자가 나오기 전까지 a에 수를 더함
                    while (splitExp[i].charAt(index) != '*') {
                        a += splitExp[i].charAt(index);
                        index++;
                    }

                    // 2. 현재 인덱스에 있는 * 연산자를 operator에 담음
                    operator = splitExp[i].charAt(index);
                    index++; // 인덱스 하나를 증가

                    // 3. * 연산자가 나오기 전까지 b에 수를 더함
                    try {
                        while (splitExp[i].charAt(index) != '*') {
                            b += splitExp[i].charAt(index);
                            index++; // 여기서 StringIndexOutOfBoundsException 오류 발생이 가능하므로 try-catch 구문으로 묶어줌
                        }
                    } catch (StringIndexOutOfBoundsException e) {}

                    // 4. 연산
                    int result;
                    if (operator == '*')
                    {
                        result = multiply(Integer.parseInt(a), Integer.parseInt(b));
                        splitStringBuilder.replace(0, index, result + ""); // 수식을 연산한 값 result로 대체함
                        splitExp[i] = splitStringBuilder.toString();
                    }
                }
            }
            expression += splitExp[i];
        }

        // 덧셈 연산을 진행
        String[] splitExp2 = expression.split("(?<=[+])|(?=[+])");

        expression = "";
        String a = "";
        String b = "";
        String operator = "";

        boolean aFilled = false; // a가 채워진 경우 true가 되는 boolean 변수
        boolean bFilled = false;
        int result;

        for (int i = 0; i < splitExp2.length; i++) {
            // 해당 문자열이 연산자일 경우
            if(splitExp2[i].equals("+"))
            {
                operator = splitExp2[i];
            }
            else
                {
                // 해당 문자열이 실수일 경우
                if(aFilled) { // a가 새로운 수로 채워진 경우
                    b = splitExp2[i];
                    bFilled = true;
                } else { // a가 아직 새로운 수로 채워지지 않은 경우
                    a = splitExp2[i];
                    aFilled = true;
                }
            }
            if(bFilled) { // b가 채워진 경우
                if(operator.equals("+"))
                {
                    result = add(Integer.parseInt(a), Integer.parseInt(b));
                    // a에 연산한 결과를 넣음
                    a = Integer.toString(result);
                    bFilled = false;
                }
            }
        }

        expression = a; // expression에 결과값을 대입
        return Integer.parseInt(expression);
    }

    public void exceptionHandler()
    {
        // 예외 처리 규칙
        // 1. 만약 [연산자]만 입력되었다면, 공백을 반환한다.
        // 2. 만약 [정수]만 입력되었다면, 실수를 반환한다. (- parser 메서드에서 처리)
        // 3. 만약 [연산자 - 정수]가 입력되었다면, 맨 앞의 연산자는 없는 것으로 취급하고 입력받은 실수를 반환한다.
        // 4. 만약 [정수 - 연산자 - 연산자 - 정수]가 입력되었다면, 첫 번째 연산자만 연산자로 취급한다.
        // 5. 만약 [정수 - 연산자]가 입력되었다면, 맨 마지막 연산자는 없는 것으로 취급한다.

        // 문자열 처리에 필요한 클래스 선언
        StringBuilder stringBuilder = new StringBuilder(expression);

        // 1번 예외를 처리
        if(!expression.matches(".*[0-9].*")) { // 정규표현식을 활용해 문자열에 숫자가 있는지 확인
            expression = "";
            return;
        }

        // 3번 예외를 처리
        if(expression.charAt(0) == '*' || expression.charAt(0) == '+') {
            expression = stringBuilder.deleteCharAt(0).toString();
        }

        // 4, 5번 예외를 처리
        int[] hasOperator = new int[expression.length()]; // 문자열의 길이만큼 int 배열을 선언
        for(int i = 0; i < expression.length(); i++) {
            // 연산자가 등장한 경우
            if(expression.charAt(i) == '*' || expression.charAt(i) == '+') {
                // 배열의 해당 인덱스를 1으로 변환
                hasOperator[i] = 1;
                if(i == expression.length() - 1) { // 맨 마지막에 연산자가 존재했다면
                    expression = stringBuilder.deleteCharAt(i).toString(); // 마지막 연산자를 삭제
                    break;
                }
                try {
                    if(hasOperator[i - 1] == 1 || hasOperator[i - 1] == 2) {
                        // 이전 인덱스가 1 혹은 2였다면, 현재 원소를 2로 변환
                        // 원소가 2인 인덱스는 삭제 대상
                        hasOperator[i] = 2;
                    }
                } catch(StringIndexOutOfBoundsException e) {}
            }
        }
        // 미리 선언한 int 배열에서 원소가 2인 인덱스를 공백으로 변경
        for (int i = 0; i < expression.length(); i++) {
            if(hasOperator[i] == 2) {
                stringBuilder.setCharAt(i, ' ');
                expression = stringBuilder.toString();
            }
        }

        // 공백을 삭제
        expression = expression.replaceAll(" ", "");

    }
}