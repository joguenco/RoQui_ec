namespace RoQuiApi.RoQui.Shared;

// Digito verificador de la clave de acceso, modulo 11, segun la ficha tecnica
// del SRI. Mismo algoritmo que el Module11 de DonPos y el PKG_MODULO11 de roteg.
public static class Module11
{
    private const int AccessKeyLength = 49;

    public static int CheckDigit(string digits)
    {
        var pivot = 2;
        var total = 0;

        // Se recorre de derecha a izquierda con pesos 2,3,4,5,6,7 y vuelta a empezar.
        for (var i = digits.Length - 1; i >= 0; i--)
        {
            if (pivot == 8)
            {
                pivot = 2;
            }

            total += (digits[i] - '0') * pivot;
            pivot++;
        }

        total = 11 - total % 11;

        return total switch
        {
            10 => 1,
            11 => 0,
            _ => total
        };
    }

    // Ni DonPos ni roteg validan: los dos solo construyen la clave y le pegan el
    // digito. Esto es lo unico nuevo.
    public static bool IsValid(string? accessKey)
    {
        if (string.IsNullOrEmpty(accessKey) || accessKey.Length != AccessKeyLength)
        {
            return false;
        }

        foreach (var character in accessKey)
        {
            if (!char.IsAsciiDigit(character))
            {
                return false;
            }
        }

        return CheckDigit(accessKey[..48]) == accessKey[48] - '0';
    }
}
