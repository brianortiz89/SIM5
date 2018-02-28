//Name   : Brian Ortiz
//Program: SIM5

//NOTE: As the lab description explained, some of the data fields will not be properly updated because
//many of the instructions have not been coded.

// This is the SHELL for the STEP class for the CPU SIM project
public class STEP{
//GLOBAL VARIABLES
  CPU cpu; // CPU is the object(instance of a class) that represents the 'STATE' of the CPU
  // these are the CPU class(Object) public attributes(variables)which represent the 'state' of the CPU
  /*byte N; // the NEGATIVE flag
   byte Z; // the ZERO flag
   byte V; // the OVERFLOW flag
   byte C; // the CARRY flag
   int A;  // the ACCUMULATOR Register
   int X;  // the INDEX Register
   int PC; // the PROGRAM COUNTER
   int SP; // the STACK POINTER (NOT USED)
   int IS; // the INSTRUCTION SPECIFIER (OPCODE)
   //String CPU.DESCR; // the INSTRUCTION mnemonic
   char MODE; // the ADDRESSING MODE
   int OS;    // the INSTRUCTION OPERAND SPECIFIER 
   int OP;    // the OPERAND DATA
   char [] MEMORY;   // the PROGRAM (Machine Instructions)
   */
  //
  boolean CO=true; boolean FO=true; boolean WO=false; // logical vars that control the stage execution
  // 
  boolean Unary=false; // to differentiate between the one/three byte type instructions
  //EA replaces MAR
  int EA=0;    // CPU the instruction/operand's memory effective address; 
  // used as the INDEX to to read/write from/to memory array
  int NEA=0;   // CPU the temp operand used for iNdirect address processing
  char DATA=0; // CPU is the value read from or to be written to memory
  int OP=0;    // used to SWITCH on the opcode in the DI() and EX() methods
//
// the 'main'>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  public static void main(String args[])
  { new SIMULATOR(); } // instantiates an object from the SIMULATOR class
// end of the 'main' >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//
// CPU method executes ONE VON NEUMANN CYCLE using/updating the CPU state passed as a parameter
// it is invoked by the SIMULATOR when the user clicks the 'STEP' button
  public void dostep(CPU cpu) { // the VonNeumann loop
    this.cpu = cpu; // the CPU state object
    FI(); // always executed
    DI(); // always executed
    if(CO) CO(); // MAY be skipped
    if(FO) FO(); // MAY be skipped
    EX(); // always executed
    if(WO) WO(); // MAY be skipped
// end of one pass of the VonNeumann cycle
    return;} // end of dostep method
//
// START OF Auxiliary methods
  void ReadMem() {
    this.DATA = cpu.MEMORY[this.EA];
    //System.out.println("Current instruction is " + cpu.DESCR+ ". ReadMem() is called and --> " + (int)this.DATA + " <-- was read from Mem[" + this.EA + "]");
    // uses the instruction/operand's Effective address to index into the MEMORY/PROGRAM array
    // in order to read one byte from that location and stores the value in the DATA variable 
  } // end ReadMem
  //
  void WriteMem(){
    cpu.MEMORY[this.EA] = this.DATA;
    //System.out.println("Current instruction is " + cpu.DESCR+ ". WriteMem() is called and --> " + (int)this.DATA + " <-- was written to Mem[" + this.EA + "]");
    // uses the instruction/operand's Effective address to index into the MEMORY/PROGRAM array
    // in order to store the one byte value in the DATA variable into that location
  } // end WriteMem
//
// START OF Instruction State METHODS
  void FI(){
    this.CO = true;
    this.FO = true;
    this.WO = false;
    cpu.C = 0;
    cpu.N = 0;
    cpu.V = 0;
    cpu.Z = 0;
    this.EA = cpu.PC;
    ReadMem();
    cpu.IS = this.DATA;
    cpu.PC++;
    this.Unary = true;
    cpu.OS = 0;
    if(!(cpu.IS == 0 || (cpu.IS >= 24 && cpu.IS <= 35)))
    {
      this.Unary = false;
      this.EA = cpu.PC;
      ReadMem();
      cpu.OS = this.DATA;
      cpu.OS = cpu.OS << 8;
      cpu.PC++;
      this.EA = cpu.PC;
      ReadMem();
      cpu.OS = cpu.OS + this.DATA;
      cpu.PC++;
    }
    //
  }// end of the FI() method
  //
  
  void DI()
  {
    //The instructions to be decoded fall into four ranges, with each range corresponding to a certain number of
    //non-terminal 'r' or 'a' bits: 0 for range 1, 2 for range 2, 3 for range 3, and 4 for range 4. The
    //following investigates the range of each instruction and calls bracket(), passing to it the corresponding
    //number of non-terminal bits.
    
    if (cpu.IS == 0){cpu.DESCR = "STOP"; cpu.MODE = ' ';}
    else if (cpu.IS >= 4 && cpu.IS <= 21){bracket(2);}
    else if (cpu.IS >= 24 && cpu.IS <= 35){bracket(3);}
    else if (cpu.IS >= 112 && cpu.IS <= 255){bracket(4);}
  }// end of the DI() method
  
  void bracket(int num)
  {
    //bracket() will now investigate (using bitwise operations) these non-terminal bits, determining, if applicable, 
    //the relevant register and addressing mode of the instruction.
    
    int temp = 0;
    if (num == 2)
    {
      this.CO = true;
      this.FO = false;
      this.WO = false;
      temp = cpu.IS >> 1;
      
      //This code is repeated below. The non-terminal bit corresponds to the least significant bit of the IS, 
      //so checking if the IS is even or odd sufficiently investigates and determines the addressing mode.
      cpu.MODE = cpu.IS % 2 == 0 ? 'i' : 'x';
      switch (temp)
      {
        case 2: cpu.DESCR = "BR"; break;
        case 3: cpu.DESCR = "BRLE"; break;
        case 4: cpu.DESCR = "BRLT"; break;
        case 5: cpu.DESCR = "BREQ"; break;
        case 6: cpu.DESCR = "BRNE"; break;
        case 7: cpu.DESCR = "BRGE"; break;
        case 8: cpu.DESCR = "BRGT"; break;
        case 9: cpu.DESCR = "BRV"; break;
        case 10: cpu.DESCR = "BRC"; break;
      }
    }
    else if (num == 3)
    {
      this.CO = false;
      this.FO = false;
      this.WO = false;
      temp = cpu.IS >> 1;
      cpu.MODE = ' ';
      switch (temp)
      {
        case 12: cpu.DESCR = "NOT"; break;
        case 13: cpu.DESCR = "NEG"; break;
        case 14: cpu.DESCR = "ASL"; break;
        case 15: cpu.DESCR = "ASR"; break;
        case 16: cpu.DESCR = "ROL"; break;
        case 17: cpu.DESCR = "ROR"; break;
      }
      char r = cpu.IS % 2 == 0 ? 'A' : 'X';
      cpu.DESCR += r;
    }
    else if (num == 4)
    {
      this.CO = true;
      this.FO = true;
      this.WO = false;
      temp = cpu.IS >> 4;
      int mMask = 7, rMask = 8;
      switch (cpu.IS & mMask)
      {
        case 0: cpu.MODE = 'i'; break;
        case 1: cpu.MODE = 'd'; break;
        case 2: cpu.MODE = 'n'; break;
        case 5: cpu.MODE = 'x'; break;
      }
      switch (temp)
      {
        case 7: cpu.DESCR = "ADD"; break;
        case 8: cpu.DESCR = "SUB"; break;
        case 9: cpu.DESCR = "AND"; break;
        case 10: cpu.DESCR = "OR"; break;
        case 11: cpu.DESCR = "CP"; break;
        case 12: cpu.DESCR = "LD"; break;
        case 13: cpu.DESCR = "LDBYTE"; break;
        case 14: this.WO = true; cpu.DESCR = "ST"; break;
        case 15: this.WO = true; cpu.DESCR = "STBYTE"; break;
      }
      char r = (cpu.IS & rMask) == 8 ? 'X' : 'A';
      cpu.DESCR += r;
    }
  }
  //
  void CO()
  {
    switch (cpu.MODE)
    {
      case 'i':
        cpu.OP = cpu.OS;
        this.EA = cpu.OS;
        this.FO = false;
        break;
      case 'd':
        this.EA = cpu.OS;
        break;
      case 'n':
        this.EA = cpu.OS;
        ReadMem();
        this.EA++;
        this.NEA = this.DATA << 8;
        ReadMem();
        this.EA = this.DATA + this.NEA;
        break;
      case 'x':
        this.EA = cpu.OS + cpu.X;
        break;
      default:
    }
    //
  }; // end of the CO() method
  //
  void FO()
  {
    //I have made significant changes to this method. In previous SIM submissions, FO() fetched both operand bytes
    //unless the instruction was LDBYTE. However, this no longer makes sense to me. Regardless of the instruction, we always want
    //the whole operand, and we let the logic of each instruction decide to act on which byte. I ran into issues with the previous
    //implementation when the first LDBYTE instruction was called. Only one of the bytes of the operand was fetched (the HO one),
    //but my code in LDBYTE was acting on the expected LO byte, which wasn't there. Since revising this method, it now works
    //perfectly.
    
    ReadMem();
    cpu.OP = this.DATA;
    //System.out.println("Current instruction is " + cpu.DESCR + ". FO() is called and the byte fetched is " + cpu.OP + ".");
    //boolean isLoadByte = cpu.DESCR.equals("LDBYTEA") || cpu.DESCR.equals("LDBYTEX");
    //if (!isLoadByte)
    //{
      this.EA++;
      cpu.OP = cpu.OP << 8;
      ReadMem();
      //System.out.println("Current instruction is " + cpu.DESCR + ". FO() is called and the byte fetched is " + (int)this.DATA + ".");
      cpu.OP += this.DATA;
      
    //}
    //
  };// end of the FO() method
  //
  void EX()
  {
    if (cpu.IS == 0){System.out.println("STOP method invoked.");}
    else if (cpu.IS >= 4 && cpu.IS <= 21)
    {
      switch (cpu.DESCR)
      {
        case "BR": BR(); break;
        case "BRLE": BRLE(); break;
        case "BRLT": BRLT(); break;
        case "BREQ": BREQ(); break;
        case "BRNE": BRNE(); break;
        case "BRGE": BRGE(); break;
        case "BRGT": BRGT(); break;
        case "BRV": BRV(); break;
        case "BRC": BRC(); break;
      }
    }
    else if (cpu.IS >= 24 && cpu.IS <= 35)
    {
      switch (cpu.DESCR.substring(0,cpu.DESCR.length()-1))
      {
        case "NOT": NOT(); break;
        case "NEG": NEG(); break;
        case "ASL": ASL(); break;
        case "ASR": ASR(); break;
        case "ROL": ROL(); break;
        case "ROR": ROR(); break;
      }
    }
    else if (cpu.IS >= 112 && cpu.IS <= 255)
    {
      switch (cpu.DESCR.substring(0,cpu.DESCR.length()-1))
      {
        case "ADD": ADD(); break;
        case "SUB": SUB(); break;
        case "AND": AND(); break;
        case "OR": OR(); break;
        case "CP": CP(); break;
        case "LD": LD(); break; 
        case "LDBYTE": LDBYTE(); break;
        case "ST": ST(); break;
        case "STBYTE": STBYTE(); break;
      }
    }
  } // end of the EX() method
  private void BR(){System.out.println("BR method invoked.");}
  private void BRLE(){System.out.println("BRLE method invoked.");}
  private void BRLT(){System.out.println("BRLT method invoked.");}
  private void BREQ(){System.out.println("BREW method invoked.");}
  private void BRNE(){System.out.println("BRNE method invoked.");}
  private void BRGE(){System.out.println("BRGE method invoked.");}
  private void BRGT(){System.out.println("BRGT method invoked.");}
  private void BRV(){System.out.println("BRV method invoked.");}
  private void BRC(){System.out.println("BRC method invoked.");}
  private void NOT(){System.out.println("NOT method invoked.");}
  private void NEG(){System.out.println("NEG method invoked.");}
  private void ASL(){System.out.println("ASL method invoked.");}
  private void ASR(){System.out.println("ASR method invoked.");}
  private void ROL(){System.out.println("ROL method invoked.");}
  private void ROR(){System.out.println("ROR method invoked.");}
  private void ADD()
  {
    boolean regA = (cpu.IS & 0x8) == 0;
    if (regA)
    {
      //V is set when operands of the same sign sum to a value with a different sign. To simulate addition of 2-byte integers, 
      //the first 2 bytes of the 4-byte integer operands are zeroed out.
      boolean v = ((cpu.A & 0x8000) == (cpu.OP & 0x8000)) && ((cpu.A & 0x8000) != (((cpu.A & 0xFFFF) + (cpu.OP & 0xFFFF)) & 0x8000));
      
      if (v) { cpu.V = 1; }
      else { cpu.V = 0; }
      
      //To simulate addition of 2-byte integers, the first 2 bytes of the 4-byte integer operands are zeroed out
      cpu.A = (cpu.A & 0xFFFF) + (cpu.OP & 0xFFFF);
      
      //C is set if a carry occurs on the most significant bit
      boolean c = (((cpu.A & 0x10000) >> 16) & 1) == 1;
      //N is set if the sum is negative
      boolean n = (short)cpu.A < 0;
      //Z is set if the sum is 0
      boolean z = (short)cpu.A == 0;
      
      if (c) { cpu.C = 1; }
      else { cpu.C = 0; }
      if (n) { cpu.N = 1; cpu.Z = 0; }
      else if (z) { cpu.Z = 1; cpu.N = 0; }
      else { cpu.N = 0; cpu.Z = 0; }
    }
    else
    {
      //V is set when operands of the same sign sum to a value with a different sign. To simulate addition of 2-byte integers, 
      //the first 2 bytes of the 4-byte integer operands are zeroed out.
      boolean v = ((cpu.X & 0x8000) == (cpu.OP & 0x8000)) && ((cpu.X & 0x8000) != (((cpu.X & 0xFFFF) + (cpu.OP & 0xFFFF)) & 0x8000));
      
      if (v) { cpu.V = 1; }
      else { cpu.V = 0; }
      
      //To simulate addition of 2-byte integers, the first 2 bytes of the 4-byte integer operands are zeroed out
      cpu.X = (cpu.X & 0xFFFF) + (cpu.OP & 0xFFFF);
      
      //C is set if a carry occurs on the most significant bit
      boolean c = (((cpu.X & 0x10000) >> 16) & 1) == 1;
      //N is set if the sum is negative
      boolean n = (short)cpu.X < 0;
      //Z is set if the sum is 0
      boolean z = (short)cpu.X == 0;
      
      if (c) { cpu.C = 1; }
      else { cpu.C = 0; }
      if (n) { cpu.N = 1; cpu.Z = 0; }
      else if (z) { cpu.Z = 1; cpu.N = 0; }
      else { cpu.N = 0; cpu.Z = 0; }
    }
  }
  
  private void SUB()
  {
    boolean regA = (cpu.IS & 0x8) == 0;
    if (regA)
    {
      //V is set when operands of the same sign sum (subtracting is adding with negative numbers) to a value with a different sign
      boolean v = ((cpu.A & 0x8000) == (cpu.OP & 0x8000)) && ((cpu.A & 0x8000) != ((cpu.A + (~(cpu.OP & 0xFFFF) + 1) & 0x8000)));
      
      if (v) { cpu.V = 1; }
      else { cpu.V = 0; }  
      
      //C is set if the sum of the reg value and the negation of the op value results in a carry on the most significant bit.
      //Again, to simulate 2-byte addition, the first two bytes of the 4-byte integers are zeroed out.
      boolean c = (((((cpu.A & 0xFFFF) + (~(cpu.OP & 0xFFFF) + 1)) & 0x10000) >> 16) & 1) == 1;
      
      //To simulate subtraction of 2-byte integers, the first 2 bytes of the 4-byte integer operands are zeroed out
      cpu.A = (cpu.A & 0xFFFF) - (cpu.OP & 0xFFFF);
      
      //N is set if the sum is negative
      boolean n = (short)cpu.A < 0;
      //Z is set if the sum is 0
      boolean z = cpu.A == 0; 
      
      if (c) { cpu.C = 1; }
      else { cpu.C = 0; }
      if (n) { cpu.N = 1; cpu.Z = 0; }
      else if (z) { cpu.Z = 1; cpu.N = 0; }
      else { cpu.N = 0; cpu.Z = 0; }
    }
    else
    {
      //V is set when operands of the same sign sum (subtracting is adding with negative numbers) to a value with a different sign
      boolean v = ((cpu.X & 0x8000) == (cpu.OP & 0x8000)) && ((cpu.X & 0x8000) != ((cpu.X + (~(cpu.OP & 0xFFFF) + 1) & 0x8000)));
      
      if (v) { cpu.V = 1; }
      else { cpu.V = 0; }  
      
      //C is set if the sum of the reg value and the negation of the op value results in a carry on the most significant bit.
      //Again, to simulate 2-byte addition, the first two bytes of the 4-byte integers are zeroed out.
      boolean c = (((((cpu.X & 0xFFFF) + (~(cpu.OP & 0xFFFF) + 1)) & 0x10000) >> 16) & 1) == 1;
      
      //To simulate subtraction of 2-byte integers, the first 2 bytes of the 4-byte integer operands are zeroed out
      cpu.X = (cpu.X & 0xFFFF) - (cpu.OP & 0xFFFF);
      
      //N is set if the sum is negative
      boolean n = (short)cpu.X < 0;
      //Z is set if the sum is 0
      boolean z = cpu.X == 0; 
      
      if (c) { cpu.C = 1; }
      else { cpu.C = 0; }
      if (n) { cpu.N = 1; cpu.Z = 0; }
      else if (z) { cpu.Z = 1; cpu.N = 0; }
      else { cpu.N = 0; cpu.Z = 0; }
    }    
  }
  private void AND()
  {
    boolean regA = (cpu.IS & 8) == 0;
    if (regA)
    {
      cpu.A = cpu.A & cpu.OP;
      boolean N = (short)cpu.A < 0;
      boolean Z = (short)cpu.A == 0;
      if (Z) {cpu.Z = 1; cpu. N = 0;}
      else if (N) {cpu.N = 1; cpu. Z = 0;}
      else {cpu.N = 0; cpu.Z = 0;}
    }
    else
    {
      cpu.X = cpu.X & cpu.OP;
      boolean N = (short)cpu.X < 0;
      boolean Z = (short)cpu.X == 0;
      if (Z) {cpu.Z = 1; cpu. N = 0;}
      else if (N) {cpu.N = 1; cpu. Z = 0;}
      else {cpu.N = 0; cpu.Z = 0;}
    }
  }
  private void OR()
  {
    boolean regA = (cpu.IS & 8) == 0;
    if (regA)
    {
      cpu.A = cpu.A | cpu.OP;
      boolean N = (short)cpu.A < 0;
      boolean Z = (short)cpu.A == 0;
      if (Z) {cpu.Z = 1; cpu. N = 0;}
      else if (N) {cpu.N = 1; cpu. Z = 0;}
      else {cpu.N = 0; cpu.Z = 0;}
    }
    else
    {
      cpu.X = cpu.X | cpu.OP;
      boolean N = (short)cpu.X < 0;
      boolean Z = (short)cpu.X == 0;
      if (Z) {cpu.Z = 1; cpu. N = 0;}
      else if (N) {cpu.N = 1; cpu. Z = 0;}
      else {cpu.N = 0; cpu.Z = 0;}
    }
  }
  private void CP()
  {
    //CP is identifical to SUB without storing the result to the register
    boolean regA = (cpu.IS & 0x8) == 0;
    if (regA)
    {
      //V is set when operands of the same sign sum (subtracting is adding with negative numbers) to a value with a different sign
      boolean v = ((cpu.A & 0x8000) == (cpu.OP & 0x8000)) && ((cpu.A & 0x8000) != ((cpu.A + (~(cpu.OP & 0xFFFF) + 1) & 0x8000)));
      
      if (v) { cpu.V = 1; }
      else { cpu.V = 0; }  
      
      //C is set if the sum of the reg value and the negation of the op value results in a carry on the most significant bit.
      //Again, to simulate 2-byte addition, the first two bytes of the 4-byte integers are zeroed out.
      boolean c = (((((cpu.A & 0xFFFF) + (~(cpu.OP & 0xFFFF) + 1)) & 0x10000) >> 16) & 1) == 1;
      
      //To simulate subtraction of 2-byte integers, the first 2 bytes of the 4-byte integer operands are zeroed out
      int result = (cpu.A & 0xFFFF) - (cpu.OP & 0xFFFF);
      
      //N is set if the sum is negative
      boolean n = (short)result < 0;
      //Z is set if the sum is 0
      boolean z = (short)result == 0; 
      
      if (c) { cpu.C = 1; }
      else { cpu.C = 0; }
      if (n) { cpu.N = 1; cpu.Z = 0; }
      else if (z) { cpu.Z = 1; cpu.N = 0; }
      else { cpu.N = 0; cpu.Z = 0; }
    }
    else
    {
      //V is set when operands of the same sign sum (subtracting is adding with negative numbers) to a value with a different sign
      boolean v = ((cpu.X & 0x8000) == (cpu.OP & 0x8000)) && ((cpu.X & 0x8000) != ((cpu.X + (~(cpu.OP & 0xFFFF) + 1) & 0x8000)));
      
      if (v) { cpu.V = 1; }
      else { cpu.V = 0; }  
      
      //C is set if the sum of the reg value and the negation of the op value results in a carry on the most significant bit.
      //Again, to simulate 2-byte addition, the first two bytes of the 4-byte integers are zeroed out.
      boolean c = (((((cpu.X & 0xFFFF) + (~(cpu.OP & 0xFFFF) + 1)) & 0x10000) >> 16) & 1) == 1;
      
      //To simulate subtraction of 2-byte integers, the first 2 bytes of the 4-byte integer operands are zeroed out
      int result = (cpu.X & 0xFFFF) - (cpu.OP & 0xFFFF);
      
      //N is set if the sum is negative
      boolean n = (short)result < 0;
      //Z is set if the sum is 0
      boolean z = (short)result == 0; 
      
      if (c) { cpu.C = 1; }
      else { cpu.C = 0; }
      if (n) { cpu.N = 1; cpu.Z = 0; }
      else if (z) { cpu.Z = 1; cpu.N = 0; }
      else { cpu.N = 0; cpu.Z = 0; }
    }
  }
  private void LD()
  {
    char r = cpu.DESCR.charAt(cpu.DESCR.length()-1);
    if (r == 'A')
    {
      cpu.A = cpu.OP;
      System.out.println(cpu.A);
      if (cpu.A == 0){cpu.Z = 1;}
      else if (((cpu.A & 0x8000) >> 15) == 1){cpu.N = 1;}
      else {cpu.Z = 0; cpu.N = 0;}
    }
    else
    {
      cpu.X = cpu.OP;
      if (cpu.X == 0){cpu.N = 0; cpu.Z = 1;}
      else if (((cpu.X & 0x8000) >> 15) == 1){cpu.N = 1; cpu.Z =0;}
      else {cpu.N = 0; cpu.Z = 0;}
    }
  }
  
  private void LDBYTE()
  {
    char r = cpu.DESCR.charAt(cpu.DESCR.length()-1);
    if (r == 'A')
    {
      cpu.A = (cpu.A & 0xFF00) + ((cpu.OP & 0x00FF));
      //System.out.println("Current instruction is " + cpu.DESCR + ". LDBYTE() is called, and the data to be loaded is " + (cpu.OP & 0x00FF) + ".");
      if (cpu.A == 0){cpu.N = 0; cpu.Z = 1;}
      else if (((cpu.A & 0x8000) >> 15) == 1){cpu.N = 1; cpu.Z=0;}
      else {cpu.N = 0; cpu.Z = 0;}
    }
    else
    {
      cpu.X = (cpu.X & 0xFF00) + (cpu.OP & 0x00FF);
      if (cpu.X == 0){cpu.N = 0; cpu.Z = 1;}
      else if (((cpu.X & 0x8000) >> 15) == 1){cpu.N = 1; cpu.Z = 0;}
      else {cpu.N = 0; cpu.Z = 0;}
    }
  }
  
  private void ST()
  {
    char r = cpu.DESCR.charAt(cpu.DESCR.length()-1);
    if (r == 'A'){cpu.OP = cpu.A;}
    else{cpu.OP = cpu.X;}
  }
  
  private void STBYTE()
  {
    char r = cpu.DESCR.charAt(cpu.DESCR.length() - 1);
    if (r == 'A') { cpu.OP = ((cpu.A & 0x00FF)); }
    else { cpu.OP = ((cpu.X & 0x00FF)); }
    //System.out.println("Current instruction is " + cpu. DESCR + ". STBYTE() is called, and the data to be stored is " + cpu.OP + ".");
  } 
  
  void WO()
  {
    this.DATA = (char)((cpu.OP & 0x00FF));
    //System.out.println("Current instruction is " + cpu. DESCR + ". WO() is called, and the LO byte to be written is " + (int)this.DATA + ".");
    WriteMem();
    boolean isStore = cpu.DESCR.equals("STBYTEA") || cpu.DESCR.equals("STBYTEX");
    if(!isStore)
    {
      EA++;
      this.DATA = (char)((cpu.OP) & 0xFF00);
      //System.out.println("Current instruction is " + cpu. DESCR + ". WO() is called, and the LO byte to be written is " + (int)this.DATA + ".");
      WriteMem();
    }
    //
  };// end of the WO()
// 
// start of individual instruction execution methods
//
} // class step
