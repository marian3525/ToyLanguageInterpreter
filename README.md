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
Example: <br>
        <code>a=readHeap(ptr)</code>. <code>a</code> now hold the value from address <code>ptr</code>
# -compound statement
Syntax:
    <code>statement1;statement2;statement3</code><br>
Example:<br>
        <code>a=10;new(addr,10);b=readHeap(addr);print(a==b)</code>. All statements separated by <code>;</code> are 
        loaded onto the execution stack in the order that they appear in the line

# -decrement/increment statement
Syntax:
    <code>var++</code> or <code>var--</code><br>
Example:<br>
    <code>a=11</code><br>
    <code>a--</code><br>
    Now a holds the value 10.
# -dereference expression
Syntax:
    <code>*addr</code> where <code>addr</code> is a heap address<br>
Example:<br>
    <code>new(ptr, 12)</code><br>
    <code>a=*ptr</code><br>
    Now <code>a</code> holds the value 12.<br>
    This operator can only be used in print and assignment statements at the moment
# -fork statement
Syntax:
    <code>fork(statement)</code><br>
Example:
    <code>a=1;new(ptr, a)</code><br>
    <code>fork(a=a+1;print(a);writeHeap(ptr, 10);print(readHeap(ptr)))</code><br>
    <code>print(a);a=a;a=a;a=a;print(readHeap(ptr))</code>
    The fork statement creates a new thread to execute the statements given as parameter. 
    The variable table is copied while the files and heap are passed by reference.
    This way any modification performed by one thread will be visible in all others.
    At the start, a=1 and 1 is stored at address <code>ptr</code> in the heap.
    When the fork statement executes, its thread will have a=2, output 2, modifiy
    the global heap writing 10 at address <code>ptr</code> and output 10.
    Back in the main function, the statement right after fork will be executing in parallel
    with the fork statement, in the main thread. It will print 1, the value of <code>a</code>
    in this thread, and print 10, the value written to the heap by the other thread.<br>
    In between the two print statements there are 3 useless statements which are used to fill
    execution time to let the other thread execute the heap writing before we read from it.<br>
    A more elegant method of achieving this would be a wait() statement which is on the todo list.