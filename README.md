# Toy Language Interpreter
A simple language interpreter written in Java. A program in this toy language is represented by a series of 
<code>Statements</code> which may operate on other <code>Statements</code> and/or <code>Expressions</code>.<br>
A <code>Statement</code> is an instruction which can be executed, such as <code>AssignmentStatement</code> (a=13).<br>
An <code>Expression</code> is an entity such as <code>ArithmeticExpression</code> (2*(a+3)) or <code>ReadHeapExpression</code> (readHeap(addr)) which can be evaluated to an integer value.
The interpreter expects statements as inputs.

# -if statements
Syntax:
	<code>if condition then statement else otherstatement</code><br>
Example (assuming <code>a</code> is defined):<br>
	<code>if a>0 then print(a*10) else a=a+1;print(a)</code>
	
# -while statement
Syntax: 
	<code>while(conditionExpression): statement</code><br>
Example (assuming <code>n</code> and <code>i</code> is defined):<br>
	<code>while(i<n): print(i);i=i+1</code><br>
	
# -print statement
Syntax:
	<code>print(expression)</code><br>
Example:<br>
	<code>print(1+3*4)</code><br>
	<code>print(readHeap(ptr)*3)</code><br>
	
# -assignment statements
Syntax:
	<code>a=expression</code><br>
Example: <br>
	<code>a=(1>=2)+1*3</code><br>
	
# -open file statements
Syntax: 
	<code>openFile(descriptorVariableName, filename)</code><br>
	Note: filename does not contain spaces or ""<br>
Example:<br>
	<code>openFile(desc, file.txt)</code><br>
	
# -read file statements
Syntax:
	<code>readFile(descr, varName)</code> where <code>descr</code> is the descriptor of the opened file and <code>varName</code> is the
	variable to read into<br>
Example (after having opened the file):<br>
	<code>readFile(descriptor, a)</code><br>

# -close file statements
Syntax:
	<code>closeFile(descriptor)</code><br>

# -allocate space into the heap
Syntax:
		<code>new(varHeapPtr, initValueExpr)</code><br>
Example:<br>
		<code>new(ptr, 11)</code>. Now at address ptr value 11 is stored<br>
# -read from the heap
Syntax:
		<code>readHeap(addrExpr)</code><br>
		
