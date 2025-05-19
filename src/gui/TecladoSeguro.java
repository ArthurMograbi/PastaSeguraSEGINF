package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Random;


public class TecladoSeguro extends JFrame {
    private java.util.List<String[]> pressedButtons = new java.util.ArrayList<>();
    private JLabel asterisksLabel = new JLabel("", SwingConstants.CENTER);
    
    public TecladoSeguro() {
        super("Cofre Digital - Autenticação");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 200);
        setLayout(new BorderLayout());

        asterisksLabel.setFont(new Font("Arial", Font.BOLD, 32));
        asterisksLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        add(asterisksLabel, BorderLayout.NORTH);

        //Teclado
        String[] digits = {"0","1","2","3","4","5","6","7","8","9"};
        JPanel keypad = new JPanel();
        keypad.setLayout(new GridLayout(2, 4));
        JButton[] buttons = new JButton[8];
        String[][] result;
        
        for (int i = 0; i < 8; i++) {
            if(i==3){ // Botão OKAY
                buttons[i] = new JButton("OKAY");
                buttons[i].addActionListener(e -> {
                    System.out.println(getPossiblePresses());
                });
                keypad.add(buttons[i]);
            } else if(i==6){  // Espaço Vazio
                buttons[i] = new JButton("");
                buttons[i].setEnabled(false);
                keypad.add(buttons[i]);
            } else if(i==7){  //Botão Limpar
                buttons[i] = new JButton("LIMPAR");
                buttons[i].addActionListener(e -> {
                    resetPressedButtons();
                });
                keypad.add(buttons[i]);
            } else{
                result = escolheDois(digits);
                digits = result[1];
                String btnLabel = result[0][0]+" E "+result[0][1];
                buttons[i]= new JButton(btnLabel) ;
                String[] selNum = new String[]{result[0][0], result[0][1]};

                buttons[i].addActionListener(e -> {
                    pressedButtons.add(selNum);
                    updateAsterisks();
                });
                keypad.add(buttons[i]);
            }
        }
        add(keypad, BorderLayout.SOUTH);
        
        setVisible(true);
    }

    public java.util.List<String> getPossiblePresses() {
        java.util.List<String> possiblePresses = new java.util.ArrayList<>();
        for (String[] pair : pressedButtons) {
            if(possiblePresses.isEmpty()){
                possiblePresses.add(pair[0]);
                possiblePresses.add(pair[1]);
            } else {
                java.util.List<String> temp = new java.util.ArrayList<>(possiblePresses);
                for (int i = 0; i < possiblePresses.size(); i++) {
                    possiblePresses.set(i, possiblePresses.get(i) + pair[0]);
                    temp.set(i, temp.get(i) + pair[1]);
                }
                possiblePresses.addAll(temp);
            }
        }
        return possiblePresses;

    }

    private String[][] escolheDois(String[] numbers) {
        System.out.println(" Current array: " + java.util.Arrays.toString(numbers));
        if (numbers.length < 2) {
            System.err.println(" Current array: " + java.util.Arrays.toString(numbers));
            throw new IllegalArgumentException("Array must have at least 2 elements");
        }
        Random rand = new Random();
        int firstIndex = rand.nextInt(numbers.length);
        int secondIndex;
        do {
            secondIndex = rand.nextInt(numbers.length);
        } while (secondIndex == firstIndex);

        String[] picked = new String[] { numbers[firstIndex], numbers[secondIndex] };

        String[] newNumbers = new String[numbers.length - 2];
        int idx = 0;
        for (int i = 0; i < numbers.length; i++) {
            if (i != firstIndex && i != secondIndex) {
                newNumbers[idx++] = numbers[i];
            }
        }

        String[][] out = {picked, newNumbers};
        return out;
    }

    private void updateAsterisks() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pressedButtons.size(); i++) {
            sb.append("*");
        }
        asterisksLabel.setText(sb.toString());
    }

    private void resetPressedButtons() {
        pressedButtons.clear();
        asterisksLabel.setText("");
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> 
            new TecladoSeguro()
        );
    }
}
