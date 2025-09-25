
import re

java_keywords = {
    "int"
}

token_specification = [
    ('IDENTIFIER', r'[a-zA-Z_]\w*'),
    ('NUMBER', r'\d+'),
    ('OPERATOR', r'==|=|\+|-|\*|/'),
    ('DELIMITER', r'[();.]''),
    ('STRING', r'"[^"]*"'),
    ('SKIP', r'[ \t]+'),  # Skip spaces/tabs
    ('MISMATCH', r'.'),  # Any other character
]

token_regex_pattern = '|'.join(f'(?P<{name}>{pattern})' for name, pattern in token_specification)


def tokenize(code):
    tokens = []
    for line in re.finditer(token_regex_pattern, code):
        cluster = line.lastgroup
        value = line.group()

        if cluster == 'SKIP':
            continue
        elif cluster == 'IDENTIFIER':
            if value in java_keywords:
                tokens.append((value, "KEYWORD"))
            else:
                tokens.append((value, "IDENTIFIER"))
        elif cluster == 'MISMATCH':
            raise RuntimeError(f'Unexpected character {value!r}')
        else:
            tokens.append((value, cluster))
    return tokens


code_snippet = ''' 
int loyaltyPoints = 200; 
int purchasePoints = 50; 
loyaltyPoints = purchasePoints; 
System.out.println("Updated Loyalty Points: " + loyaltyPoints); 
'''

for line in code_snippet.splitlines():
    if line.strip(): print(line.strip(), "→", tokenize(line))

Syntax Analyzer
class Parser:
    def __init__(self, tokens):
        self.tokens = tokens
        self.pos = 0
        self.symbols = set()

    def peek(self):
        return self.tokens[self.pos] if self.pos < len(self.tokens) else (None, None)

    def eat(self, expected_value=None, expected_type=None):
        value, cluster = self.peek()
        if expected_value and value != expected_value:
            raise SyntaxError(f"Expected '{expected_value}' but got '{value}'")
        if expected_type and cluster != expected_type:
            raise SyntaxError(f"Expected {expected_type} but got {cluster}")
        self.pos += 1
        return value

    def parse_program(self):
        while self.pos < len(self.tokens):
            self.parse_statement()

    def parse_statement(self):
        value, cluster = self.peek()
        if value == "int":  # variable declaration
            self.parse_var_decl()
        elif value == "System":  # print statement
            self.parse_print()
        elif cluster == "IDENTIFIER":  # assignment
            lowered = value.lower()
            if lowered in ["systemout", "systemoutprintln"]:
                self.parse_print()
            else:
                self.parse_assign()

        else:
            raise SyntaxError(f"Unexpected token {value}")

    def parse_var_decl(self):
        self.eat("int")  # keyword int
        self.symbols.add(self.eat(expected_type="IDENTIFIER"))  # variable name
        self.eat("=")  # =
        self.eat(expected_type="NUMBER")  # number literal
        self.eat(";")  # must end with ;

    def parse_assign(self):
        # Eat the variable being assigned and check declaration in one step
        if (var_name := self.eat(expected_type="IDENTIFIER")) not in self.symbols:
            raise SyntaxError(f"Variable '{var_name}' not declared before use")

        self.eat("=")

        value, cluster = self.peek()
        if cluster == "IDENTIFIER":
            # Eat the right-hand side identifier and check declaration
            if (ref_name := self.eat(expected_type="IDENTIFIER")) not in self.symbols:
                raise SyntaxError(f"Variable '{ref_name}' not declared before use")
        elif cluster == "NUMBER":
            self.eat(expected_type="NUMBER")
        else:
            raise SyntaxError(f"Expected identifier or number but got '{value}'")

        self.eat(";")

    def parse_print(self):
        value, cluster = self.peek()

        # this is a special condition raised if "." is not between 'System' and 'out'

        # Check:
        if cluster == "IDENTIFIER":
            lowered = value.lower()
            if lowered == "systemout":
                raise SyntaxError("Missing '.' between 'System' and 'out'")
            elif lowered == "systemoutprintln":
                raise SyntaxError("Missing '.' between 'System', 'out', and 'println'")

        # Normal expected sequence
        self.eat("System")
        self.eat(".")
        self.eat("out")
        self.eat(".")
        self.eat("println")
        self.eat("(")
        self.eat(expected_type="STRING")
        if self.peek()[0] == "+":  # optional concatenation
            self.eat("+")
            if (var_name := self.eat(expected_type="IDENTIFIER")) not in self.symbols:
                raise SyntaxError(f"Variable '{var_name}' not declared before use")
        self.eat(")")
        self.eat(";")


tokens = tokenize('''
int loyaltyPoints = 200;
int purchasePoints = 50;
loyaltyPoints = purchasePoints;
System.out.println("Updated Loyalty Points: " + loyaltyPoints);
''')

parser = Parser(tokens)

try:
    parser.parse_program()
    print("Syntax is correct")
except SyntaxError as e:
    print("Syntax error:", e)
