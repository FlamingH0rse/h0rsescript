namespace h0rsescript.parser
{
    static class Tokenizer
    {
        // Initialize class
        private static string Src = "";
        private static int Pos = 0;
        private static List<Token> Tokens = [];
        
        // Helper properties
        private static char Current
        {
            get => Src[Pos];
        }
        private static char Previous
        {
            get => Src[Pos - 1];
        }
        private static bool IsEOF() => Pos >= Src.Length;
        private static char? Next
        {
            get => Pos < Src.Length ? Src[Pos + 1] : null;
        }

        // Language-defined constants
        private const char KeywordPrefix = '$';
        private static readonly string[] Keywords =
            (new string[] { "define", "end", "parameters", "return", "mode", "include" }).Select(k => KeywordPrefix + k).ToArray();
        private static readonly string[] AssignmentOperators =
            { "->", "<->", ">", "<-", "<" };
        private static readonly string[] Booleans =
            { "TRUE", "FALSE" };

        // Consume token
        private static void Consume(Func<char?> getTokenValue, TokenType tokenType, char? lastTokenToValidate = null)
        {
            int startPos = Pos;
            // Consume current character and move on
            string val = Current.ToString();
            Pos++;

            // Consume subsequent characters until null
            while (!IsEOF() && getTokenValue() != null)
            {
                val += getTokenValue();
                Pos++;
            }
            // Consume last character if it matches lastValidToken
            if (!IsEOF() && lastTokenToValidate != null && Current == lastTokenToValidate)
            {
                val += Current;
                Pos++;
            } else
            {
                // Throw Error
                // "Unexpected EOF at position {Pos-1}, where {lastValidToken} was expected"
            }

            Tokens.Add(new Token(Src, val, startPos, tokenType));
        }

        // Consume token and validate from a set of values
        private static void ConsumeAndValidate(Func<char?> getTokenValue, TokenType tokenType, string[] validValues)
        {
            Consume(getTokenValue, tokenType);
            
            // Validate token
            if (!validValues.Contains(Tokens.Last().Val))
            {
                // THROW ERROR
                Console.WriteLine($"WEEWOO1 {Tokens.Last().Val} at {Tokens.Last().Pos}");
                
            }
        }

        // Tokenize
        public static List<Token> Tokenize(string Str)
        {
            Src = Str;
            while (!IsEOF())
            {
                // Identifiers, Booleans and Qualified Identifiers
                if (char.IsLetter(Current) || Current == '_')
                {
                    Consume(GetIdentifier, TokenType.IDENTIFIER);
                    // Handle Booleans
                    if (Booleans.Contains(Tokens.Last().Val))
                    {
                        Token boolToken = Tokens.Last() with { Type = TokenType.BOOLEAN };
                        Tokens[^1] = boolToken;
                    }
                    // Handle Qualified Identifiers
                    if (Tokens.Last().Val.Contains('.'))
                    {
                        Token qIdentifierToken = Tokens.Last() with { Type = TokenType.QUALIFIED_IDENTIFIER };
                        Tokens[^1] = qIdentifierToken;
                    }
                }
                // String Literal
                else if (Current == '"')
                {
                    Consume(GetStringLiteral, TokenType.STRING, '"');
                }
                // Number Literal
                else if (char.IsDigit(Current) || Current == '-' && char.IsDigit(Next.GetValueOrDefault()))
                {
                    Consume(GetNumberLiteral, TokenType.NUMBER);
                }
                // Keywords
                else if (Current == KeywordPrefix)
                {
                    ConsumeAndValidate(GetIdentifier, TokenType.KEYWORD, Keywords);
                }
                // Assignment Operators
                else if (Current == '<' || Current == '-' || Current == '>')
                {
                    ConsumeAndValidate(GetAssignmentOperator, TokenType.ASSIGNMENT_OPERATOR, AssignmentOperators);
                }
                // Brackets & Commas
                else if (Current == '[')
                {
                    Consume(GetNull, TokenType.OPEN_BRACKET);
                }
                else if (Current == ']')
                {
                    Consume(GetNull, TokenType.CLOSE_BRACKET);
                }
                else if (Current == '{')
                {
                    Consume(GetNull, TokenType.OPEN_CURLY);
                }
                else if (Current == '}')
                {
                    Consume(GetNull, TokenType.CLOSE_CURLY);
                }
                else if (Current == ',')
                {
                    Consume(GetNull, TokenType.COMMA);
                }
                // Whitespace
                else if (char.IsWhiteSpace(Current))
                {
                    Pos++;
                }

                // Comments
                else if (Current == '#')
                {
                    Consume(GetComment, TokenType.COMMENT);
                }
                else
                {
                    // Throw InvalidTokenError TODO
                    Console.WriteLine($"WEEWOO `{Current}`");
                }
            }
            return Tokens;
        }

        // Token helper functions return the current char/null if a certain condition is true/false

        // Identifier : Returns all characters that matches the regex [a-zA-Z0-9_]
        private static char? GetIdentifier()
        {
            Func<char, bool> isValidIdentifierChar = (c) => char.IsLetterOrDigit(c) || c == '_';

            // GetValueOrDefault converts null to null char '\0' which is not a valid identifier char
            if (isValidIdentifierChar(Current) || (Current == '.' && isValidIdentifierChar(Next.GetValueOrDefault())))
                return Current;
            else
                return null;
        }

        // Assignment Operator : Returns the only possible subsequent characters i.e '-' and '>'
        // RIP "Goofy ahh code" 29 Dec 2024 - 14 Jun 2026
        private static char? GetAssignmentOperator()
        {
            if (Current == '-' || Current == '>')
                return Current;
            else
                return null;
        }

        // String : Returns all characters up to `"`
        private static char? GetStringLiteral()
        {
            // Handles escape sequences with `"`
            if (Current != '"' || (Current == '"' && Previous == '\\'))
                return Current;
            else
                return null;
        }

        // Number : Returns the digits 0-9 or '.' if a digit exists after it
        private static char? GetNumberLiteral()
        {
            // GetValueOrDefault converts null to null char '\0' which is not a digit
            if (char.IsDigit(Current) || (Current == '.' && char.IsDigit(Next.GetValueOrDefault())))
                return Current;
            else
                return null;
        }
        // Comment : Returns all characters except line break `\n`
        private static char? GetComment()
        {
            if (Current != '\n')
                return Current;
            else
                return null;
        }

        // GetNull : Used single char tokens that have no subsequent chars to consume
        private static char? GetNull() => null;
    }

    // Token & TokenType classes
    public record Token(string Src, string Val, int Pos, TokenType Type)
    {
        public override string ToString()
        {
            return $"Token {{ Val: {Val}, Pos: {Pos}, Type: {Type} }}";
        }
    };
    public enum TokenType
    {
        IDENTIFIER,    // Variable or function names
        QUALIFIED_IDENTIFIER, // Function signatures like math.add
        KEYWORD,       // Keywords like $define, $end, etc.

        ASSIGNMENT_OPERATOR,        // Symbols like ->, <->, etc.

        STRING,        // String literals
        NUMBER,        // Numeric literals
        BOOLEAN,       // TRUE/FALSE

        OPEN_BRACKET,  // [
        CLOSE_BRACKET, // ]
        OPEN_CURLY,    // {
        CLOSE_CURLY,   // }
        COMMA,         // ,

        COMMENT,       // Comments
    }

}
