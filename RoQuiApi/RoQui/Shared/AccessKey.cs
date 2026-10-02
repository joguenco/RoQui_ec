namespace RoQuiApi.RoQui.Shared;

using System.Globalization;

// Los 9 campos de la clave de acceso, tabla 1 de la ficha tecnica del SRI.
// Cada campo es un trozo de la misma cadena de 49 digitos:
//
//   29092026 01 0402197214001 1 001001 000000901 12345678 1 9
//   fecha    doc  ruc        amb serie secuencial  codigo  e v
public static class AccessKey
{
    private const int Length = 49;

    // Tabla 3: los comprobantes que se pueden emitir.
    private static readonly string[] DocumentCodes = ["01", "03", "04", "05", "06", "07"];

    // Tabla 4: 1 pruebas, 2 produccion.
    private static readonly string[] Environments = ["1", "2"];

    // Tabla 2: por ahora solo existe la emision normal.
    private const string NormalEmission = "1";

    // El codigo de cada documento y su codDoc de la tabla 3. Son los de DonPos,
    // roteg los traduce en PKG_ROQUI antes de mandar.
    private static readonly Dictionary<string, string> CodeToDocument = new()
    {
        ["FV"] = "01",
        ["LQ"] = "03",
        ["DV"] = "04",
        ["NC"] = "04",
        ["ND"] = "05",
        ["GUI"] = "06",
        ["RT"] = "07",
    };

    // La fecha, el codigo y el numero se usan para armar la clave, asi que tienen
    // que coincidir con lo que viaja dentro. Si no, el documento va a decir una
    // cosa y su clave otra. La identificacion del comprador no se compara: la
    // clave lleva el RUC del emisor, no el suyo. documentCode es el tipo que
    // espera el endpoint: 01 en factura, 04 en nota de credito...
    public static IEnumerable<string> ValidateAgainst(
        string? accessKey, string? code, string? number, DateTime date, string documentCode)
    {
        // Si la clave ya viene mal, el mensaje de Validate es el que importa.
        if (Validate(accessKey) is not null)
        {
            yield break;
        }

        if (accessKey![..8] != date.ToString("ddMMyyyy", CultureInfo.InvariantCulture))
        {
            yield return "The date does not match the one in the accessKey.";
        }

        // Cada endpoint acepta solo sus codigos: la factura FV, la nota de credito
        // DV o NC... Un LIQ sin traducir tampoco pasa.
        var allowedCodes = CodeToDocument
            .Where(entry => entry.Value == documentCode)
            .Select(entry => entry.Key)
            .ToList();
        if (code is null || !allowedCodes.Contains(code))
        {
            yield return $"The code {code} is not valid, it must be {string.Join(" or ", allowedCodes)}.";
        }
        else if (accessKey[8..10] != documentCode)
        {
            yield return $"The code {code} must be {documentCode} in the accessKey.";
        }

        if (accessKey[24..39] != number)
        {
            yield return "The number does not match the one in the accessKey.";
        }
    }

    // El RUC del emisor, posiciones 11 a 23. Null si la clave no son 49 digitos.
    public static string? Ruc(string? accessKey)
    {
        return accessKey is { Length: Length } && accessKey.All(char.IsAsciiDigit)
            ? accessKey[10..23]
            : null;
    }

    // Devuelve null si la clave esta bien, o el motivo si no.
    public static string? Validate(string? accessKey)
    {
        if (string.IsNullOrEmpty(accessKey) || accessKey.Length != Length)
        {
            return "The access Key must contain exactly 49 digits.";
        }

        foreach (var character in accessKey)
        {
            if (!char.IsAsciiDigit(character))
            {
                return "The accesskey must contain only digits.";
            }
        }

        // 1. Fecha de emision, ddmmaaaa. No basta con que sean 8 digitos: 32132026
        // tambien lo son.
        if (!DateTime.TryParseExact(
                accessKey[..8],
                "ddMMyyyy",
                CultureInfo.InvariantCulture,
                DateTimeStyles.None,
                out _))
        {
            return "The issue date in the accessKey is not a valid date.";
        }

        // 2. Tipo de comprobante.
        if (!DocumentCodes.Contains(accessKey[8..10]))
        {
            return "The document type in the accessKey is not valid.";
        }

        // 3. RUC del emisor. El digito verificador no se comprueba aqui: son 13
        // digitos, la provincia y el establecimiento.
        var province = int.Parse(accessKey[10..12]);
        if (province is not (>= 1 and <= 24) and not 30)
        {
            return "The province in the accessKey RUC is not valid.";
        }

        if (accessKey[20..23] == "000")
        {
            return "The establishment in the accessKey RUC cannot be 000.";
        }

        // 4. Tipo de ambiente.
        if (!Environments.Contains(accessKey[23..24]))
        {
            return "The environment in the accessKey must be 1 or 2.";
        }

        // 5. Serie del establecimiento y punto de emision.
        if (accessKey[24..30] == "000000")
        {
            return "The series in the accessKey cannot be zeros.";
        }

        // 6. Secuencial.
        if (accessKey[30..39] == "000000000")
        {
            return "The sequential in the accessKey cannot be zeros.";
        }

        // 7. El codigo numerico son 8 digitos y ya quedaron comprobados arriba.

        // 8. Tipo de emision.
        if (accessKey[47..48] != NormalEmission)
        {
            return "The emission type in the accessKey must be 1.";
        }

        // 9. Digito verificador.
        if (Module11.CheckDigit(accessKey[..48]) != accessKey[48] - '0')
        {
            return "The accessKey check digit is not valid.";
        }

        return null;
    }
}
