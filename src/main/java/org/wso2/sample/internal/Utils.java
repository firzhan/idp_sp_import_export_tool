package org.wso2.sample.internal;

import java.util.Scanner;

public class Utils {

    public static int readInput(String question, String pattern){

        while (true){

            Scanner scanner = new Scanner(System.in);
            System.out.println(question + "\n");
            System.out.print("Answer: >> ");

            if(scanner.hasNext(pattern)){
                System.out.println("\n");
                return scanner.nextInt();
            } else {
                if(scanner.hasNext("[xX]")){
                    System.out.println("Execution Aborted");
                    System.exit(0);
                } else {
                    System.err.println("Invalid input has been entered. Please enter the correct value when prompted again");
                }
            }

        }
    }
}
