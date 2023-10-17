import java.util.*;

class Main {
  public static void main(String[] args) {
    writeBorder("=", 40);
    System.out.print("WELCOME TO MASTERMIND");
    writeBorder("=",40);
    
    System.out.println("\n");
    
    char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    
    Mastermind(alphabet);

   } //main




  
  //Just decoration, helps with separation
  public static void writeBorder(String symbol, int length){
    if(length == 0)
      System.out.println("");
    else if (length == 1)
      System.out.print(symbol);
    else{
      System.out.print(symbol);
      writeBorder(symbol, length - 1);
    }
  }

  //MASTER FUNCTION
  public static void Mastermind( char[] gameLetters) {
    Scanner kbd = new Scanner(System.in);
    boolean gameOver = false;
    int roundCount = 1;
    while(gameOver == false) {
      writeBorder("\t",6);
      System.out.printf("ROUND %d\n\n", roundCount);
      round(kbd, gameLetters);
      System.out.println("Would you like to play another round? Y/N\t");
      String again = kbd.next();
      if (again.toLowerCase().equals("n")){
        gameOver = true;
      } else {roundCount++;}
        
    }
  }
  
//Really long function, but it houses alot of info and does everything 
  //needed for a round in correct order
  public static void round(Scanner kbd, char[] gameLetters) {
    int codeLength = getCodeLength(kbd);
    int numLetters = getLetterCount(kbd);
    boolean dupesMode =  isInDupesMode(kbd);

    if(codeLength > numLetters && dupesMode == false) {
      System.out.println("Code length cannot be greater than the number of letters without duplicates.") ;
      codeLength = 4 ;
      numLetters = 6 ;
    }
    
    char[] letterOptions = getLetterList(numLetters, gameLetters);
    int difficultyLevel = getDifficulty(codeLength,numLetters, dupesMode);
    ArrayList<Character> target = getTargetSequence(codeLength, numLetters, dupesMode, letterOptions);

    
    int attemptCount = getNumOfAttempts(difficultyLevel);
    System.out.printf("You have %d attempts.", attemptCount);                                                                        
    boolean win = false;
    for (int i = 1; i < attemptCount + 1; i++){
      System.out.println("\nLetters to choose from:\t\n" + Arrays.toString(letterOptions));

      writeBorder("\t",6);
      System.out.println("Attempt #" + i);

      win = attempt(kbd, target, dupesMode);
      if(win == true) {
        System.out.println("Congratulations, you're a Mastermind!");
        return;
      }
      if(i == attemptCount){
        System.out.println("Sorry, you lost this round.");
        System.out.println("This was the target:\t" + target);
      }
      writeBorder("-",35);
    }
    return;
  }


  public static char[] getLetterList(int numLetters, char[] alphabets) {
    char[] letters = Arrays.copyOf(alphabets, alphabets.length);
    Random randInt = new Random();

      
    for (int i = 0; i < letters.length - 1; i++) {
      int j = randInt.nextInt(letters.length - i) + i;
      char temp = letters[i];
      letters[i] = letters[j];
      letters[j] = temp;
    }

     
    return Arrays.copyOf(letters, numLetters);
  }


  public static char[] shuffleLetters(char[] letters, boolean mode) {
    char[] shuffledLetters = Arrays.copyOf(letters, letters.length);
    Random randInt = new Random();

    if (mode == true) {
      for (int i = shuffledLetters.length - 1; i > 0; i--) {
        int j = randInt.nextInt(i + 1);
        char temp = shuffledLetters[i];
        shuffledLetters[i] = shuffledLetters[j];
        shuffledLetters[j] = temp;
      }
    }

    return shuffledLetters;
  } 


  //function to generate the target sequence depending on whether the user wants dupes
  public static ArrayList<Character> getTargetSequence(int codeL, int numL, boolean mode, char[] letterList) {
    ArrayList<Character> result = new ArrayList<>();
    Random randInt = new Random();
    char[] shuffledLetters = shuffleLetters(letterList, mode);

    for (int i = 0; i < codeL; i++) {
      char ch;

      if (mode == true) {
        ch = shuffledLetters[randInt.nextInt(numL)];
      } else {
        int numC = randInt.nextInt(numL);
        ch = shuffledLetters[numC];
        shuffledLetters[numC] = shuffledLetters[numL - 1];
        numL = numL - 1;
      }

      result.add(ch);
    }

    return result;
  }


 

  //function to return the code length
  public static int getCodeLength (Scanner console) {
    System.out.print("Please enter a code length of 4, 6, or 8: ") ;
    int codeLen = console.nextInt() ;

    if(codeLen != 4 && codeLen != 6 && codeLen != 8 ) {
      codeLen = 4;
      System.out.println("Invalid number, default code length is 4") ;
   
    } //if statement

    return codeLen;
    

    
  } //function call

  //You did a really good job of handling when a 
  //user puts in invalid input!
    //function that returns the letter count based on user input
  public static int getLetterCount (Scanner console) {
    System.out.print("Please enter the number of letters you would like to use 6, 7, or 8? ") ;
    int numLetters = console.nextInt() ;

    if(!(numLetters >= 6 && numLetters <= 8)) {
      numLetters = 6 ;
      System.out.println ("Invalid number, default letter length is 6") ;

    } //if 


    return numLetters ;

    
  } //function call

  
  public static boolean isInDupesMode(Scanner console) {
    System.out.print("Would you like to allow duplicates? Y/N ");
    String dupeAnswer = console.next();
    boolean dupes = false;

    if(dupeAnswer.toLowerCase().equals("y")) {
      dupes = true;
      System.out.println("You are now playing in duplicates mode.");
    } else {
      System.out.println("You are playing in no duplicates mode.");
    }
    return dupes;
  }

  //Increasing difficulty if in dupesMode
  public static int getDifficulty(int codeLen, int letterOptionCount, boolean mode) {
    int total = codeLen + letterOptionCount;
    int difficulty;
    
    if(total <= 11){
      difficulty = 8;
    } else if(total <= 13) {
      difficulty = 10;
    } else {
      difficulty = 12;
    }
    
    if(mode == true) {
      difficulty +=2;
    }
    return difficulty;
  }

    //function to determine the correct placement of user input
  public static boolean attempt(Scanner console, ArrayList<Character> targetCode, boolean mode) {
    String userAttempt = console.next().toUpperCase();
    int sizeNeeded = targetCode.size();
    boolean correctAttempt = false;
    boolean giveFeedback = true;

    //Truncating if input is longer than selected targetCode size
    if(userAttempt.length() > sizeNeeded) {
      userAttempt = userAttempt.substring(0, sizeNeeded);
      System.out.printf("Mastermind will only consider the first %d letters of your attempt.\n\n", sizeNeeded);
    //Not giving feedback on short answers to avoid null pointer error  
    } else if(userAttempt.length() < sizeNeeded) {
      System.out.printf("You did not input at least %d letters. No feedback for this attempt.\n\n", sizeNeeded);
      giveFeedback = false;
    }

    if(userAttempt.equals(targetCode.toString().replaceAll(", ","").substring(1,sizeNeeded+1))) {
      correctAttempt = true;
    }
    if(giveFeedback == true)
      attemptFeedback(userAttempt,targetCode, mode);

    return correctAttempt;
  }

  
  public static int getNumOfAttempts(int difficultyLevel) {
    int num = 0;

    if (difficultyLevel <= 12){
      num = 8;
    } else if (difficultyLevel <= 14) {
      num = 10;
    } else if (difficultyLevel <= 16) {
      num = 12;
    }

    return num;
  }
  
  public static void attemptFeedback(String userAttempt, ArrayList<Character> targetCode, boolean mode){
    Deque<String> feedback = new ArrayDeque<>();
    Set<Character> noDupeFeed = new HashSet<>();

    for(int i = 0; i < targetCode.size(); i ++) {
      char userLetter = userAttempt.charAt(i);
      noDupeFeed.add(userLetter);
      if(userLetter == targetCode.get(i)){
          feedback.addFirst("r");
      } else if (targetCode.contains(userLetter)){
        if (mode && !noDupeFeed.add(userLetter)) {
            feedback.addLast("");
        } else {
        feedback.add("w");
        }
      }
    } 
    System.out.println("Feedback: " + feedback);
  }


} //class