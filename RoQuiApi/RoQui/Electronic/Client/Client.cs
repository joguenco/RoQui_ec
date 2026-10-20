namespace RoQuiApi.RoQui.Electronic.Client;

using System.Net.Http.Json;
using RoQuiApi.RoQui.Electronic.Repository;

public class Client<T>
{
    private static readonly HttpClient HttpClient = new();

    public static async Task Authorize(
        string documentUrl,
        string code,
        string number,
        IElectronicRepo electronicRepo,
        ILogger<T> logger)
    {
        try
        {
            var url = electronicRepo.GetParameterByName("RoQui HTTP Server");
            var apiKey = electronicRepo.GetParameterByName("RoQui HTTP X-API-KEY");

            if (string.IsNullOrWhiteSpace(url?.Value) || string.IsNullOrWhiteSpace(apiKey?.Value))
            {
                throw new InvalidOperationException("Invalid configuration for electronic document authorization");
            }

            var authorizeUrl = url.Value.TrimEnd('/') + documentUrl;

            using var content = JsonContent.Create(new { code, number });
            using var request = new HttpRequestMessage(HttpMethod.Post, authorizeUrl)
            {
                Content = content
            };
            request.Headers.Add("X-API-KEY", apiKey.Value);
            var response = await HttpClient.SendAsync(request);
            logger.LogInformation("Authorize invoice {code}-{number}: {status}", code, number, response.StatusCode);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Error authorizing invoice {code}-{number}", code, number);
        }
    }
}