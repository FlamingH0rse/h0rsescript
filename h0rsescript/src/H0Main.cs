using h0rsescript.parser;

namespace h0rsescript
{
    class H0Main
    {
        const string LANG_NAME = "h0rsescript";
        const string VERSION = "0.0.1";
        static void Main(string[] args)
        {
            if (args.Length == 0)
            {
                Console.WriteLine($"Welcome to {LANG_NAME} v{VERSION}");
                Console.WriteLine("Type h0 <file_name> to run it.");
                
                while (true)
                {
                    Console.Write(">> ");
                    var command = Console.ReadLine();
                    Console.WriteLine($"Running {command}...");
                    // Run interpreter

                }
            }
            else
            {
                if(!File.Exists(args[0]))
                {
                    Console.WriteLine("File no exist");
                    return;
                }
                string src = File.ReadAllText(args[0]);
                var tokens = Tokenizer.Tokenize(src);

                tokens.ForEach(t => Console.WriteLine(t));
            }
        }
    }
}