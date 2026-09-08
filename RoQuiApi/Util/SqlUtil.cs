namespace RoQuiApi.Util;

public static class SqlUtil
{
    public static string SqlResource(string sqlFileName)
    {
        var codeBase = Directory.GetCurrentDirectory();
        var separator = Path.DirectorySeparatorChar.ToString();
        var concatenatedPath = $"{codeBase}{separator}Sql{separator}{sqlFileName}";

        return concatenatedPath;
    }
}