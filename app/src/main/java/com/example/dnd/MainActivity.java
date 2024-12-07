package com.example.dnd;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Spinner diceSpinner;
    private EditText numDiceInput;
    private EditText successConditionInput;
    private EditText enhancementInput; // Поле для усилений
    private Button rollButton;
    private Button toggleSignButton; // Кнопка для переключения знака
    private TextView resultText;
    private ListView historyList;

    private ArrayList<String> rollHistory;
    private ArrayAdapter<String> historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов интерфейса
        diceSpinner = findViewById(R.id.dice_spinner);
        numDiceInput = findViewById(R.id.num_dice_input);
        successConditionInput = findViewById(R.id.success_condition_input);
        enhancementInput = findViewById(R.id.enhancement_input);
        rollButton = findViewById(R.id.roll_button);
        toggleSignButton = findViewById(R.id.toggle_sign_button); // Инициализация кнопки для переключения знака
        resultText = findViewById(R.id.result_text);
        historyList = findViewById(R.id.history_list);

        // Настройка выпадающего списка
        String[] diceOptions = {"d4", "d6", "d8", "d10", "d12", "d20"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, diceOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diceSpinner.setAdapter(adapter);

        // Настройка списка истории
        rollHistory = new ArrayList<>();
        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rollHistory);
        historyList.setAdapter(historyAdapter);

        // Обработка нажатия кнопки "Бросить"
        rollButton.setOnClickListener(v -> rollDice());

        // Обработка нажатия кнопки для переключения знака
        toggleSignButton.setOnClickListener(v -> {
            // Получаем текущее значение в поле ввода усиления
            String currentText = enhancementInput.getText().toString();

            // Проверяем, если поле пустое, просто ставим "+"
            if (currentText.isEmpty()) {
                enhancementInput.setText("+");
                toggleSignButton.setText("+"); // Устанавливаем знак "+"
            } else if (currentText.startsWith("+")) {
                // Если уже есть + в начале, меняем на -
                enhancementInput.setText("-" + currentText.substring(1));
                toggleSignButton.setText("-"); // Меняем текст на "-"
            } else if (currentText.startsWith("-")) {
                // Если есть - в начале, меняем на +
                enhancementInput.setText("+" + currentText.substring(1));
                toggleSignButton.setText("+"); // Меняем текст на "+"
            }
        });
    }

    private void rollDice() {
        String selectedDice = diceSpinner.getSelectedItem().toString();
        int maxFace = Integer.parseInt(selectedDice.substring(1)); // Извлекаем число (например, из "d6" берем 6)

        String numDiceStr = numDiceInput.getText().toString();
        if (numDiceStr.isEmpty()) {
            resultText.setText("Введите количество кубиков!");
            return;
        }

        // Получаем значение из поля "УСЛОВИЕ"
        String successConditionStr = successConditionInput.getText().toString();
        int successCondition = 0;
        if (!successConditionStr.isEmpty()) {
            successCondition = Integer.parseInt(successConditionStr);
        }

        // Получаем значение из поля "Усиление"
        String enhancementStr = enhancementInput.getText().toString();
        int enhancement = 0;
        if (!enhancementStr.isEmpty()) {
            try {
                enhancement = Integer.parseInt(enhancementStr); // Преобразуем в число
            } catch (NumberFormatException e) {
                enhancement = 0; // Если введено что-то невалидное, считаем усиление равным 0
            }
        }

        int numDice = Integer.parseInt(numDiceStr);
        Random random = new Random();
        StringBuilder results = new StringBuilder("Результаты: ");
        StringBuilder colorResults = new StringBuilder();
        StringBuilder historyResults = new StringBuilder();

        for (int i = 0; i < numDice; i++) {
            int roll = random.nextInt(maxFace) + 1; // Генерация числа от 1 до maxFace
            roll += enhancement; // Применяем усиление

            // Обрабатываем результаты в зависимости от значения
            String rollText = String.valueOf(roll);

            // Определяем цвет для текста
            if (roll == 1) {
                rollText = "<font color='#FF0000'>" + rollText + "</font>"; // Красный для 1
            } else if (roll == 20) {
                rollText = "<font color='#FF9800'>" + rollText + "</font>"; // Оранжевый для 20
            } else if (roll > successCondition) {
                rollText = "<font color='#4CAF50'>" + rollText + "</font>"; // Зеленый для успеха
            }

            colorResults.append(rollText).append(" ");
            historyResults.append(roll).append(" ");
        }

        // Преобразуем строку с результатами в HTML, чтобы использовать цвета
        resultText.setText(android.text.Html.fromHtml(colorResults.toString(), android.text.Html.FROM_HTML_MODE_LEGACY));

        // Сохранение в историю
        String resultString = historyResults.toString();
        String historyEntry = numDice + " x " + selectedDice + " → " + resultString.trim();
        rollHistory.add(0, historyEntry); // Добавляем в начало списка
        if (rollHistory.size() > 10) {
            rollHistory.remove(rollHistory.size() - 1); // Удаляем старые записи, если их больше 10
        }
        historyAdapter.notifyDataSetChanged();
    }
}
