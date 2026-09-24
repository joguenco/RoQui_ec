namespace RoQuiApi.RoQui.Security;

using System.Security.Cryptography;
using System.Text;
using RoQuiApi.RoQui.Electronic.Repository;

public class ApiKeyValidator : IApiKeyValidator
{
    // La clave sale de ele_parameters, la misma fila que ya usa Client.cs para
    // llamar a RoQui. Asi no queda la clave en dos sitios distintos.
    private const string ParameterName = "RoQui HTTP X-API-KEY";

    private readonly IElectronicRepo _electronicRepo;

    public ApiKeyValidator(IElectronicRepo electronicRepo)
    {
        _electronicRepo = electronicRepo;
    }

    public bool IsValid(string? requestApiKey)
    {
        var apiKey = _electronicRepo.GetParameterByName(ParameterName)?.Value;

        // Si no hay clave configurada no dejo entrar a nadie. Prefiero que deje de
        // funcionar por falta de configuracion antes que quedar abierto sin enterarme.
        if (string.IsNullOrWhiteSpace(requestApiKey) || string.IsNullOrWhiteSpace(apiKey))
        {
            return false;
        }

        // Comparo en tiempo constante, igual que el MessageDigest.isEqual de RoQui.
        // Con un == normal se puede adivinar la clave letra por letra midiendo tiempos.
        return CryptographicOperations.FixedTimeEquals(
            Encoding.UTF8.GetBytes(requestApiKey),
            Encoding.UTF8.GetBytes(apiKey));
    }
}
