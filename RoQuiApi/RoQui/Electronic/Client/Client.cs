namespace RoQuiApi.RoQui.Electronic.Client;

using System.Net.Http.Json;
using RoQuiApi.RoQui.Electronic.Repository;

public static class Client
{
    private static readonly HttpClient HttpClient = new();

    public static async Task Authorize(
        string documentUrl, 
        string code, 
        string number,
        IElectronicRepo electronicRepo)
    {
        try
        {
            var url = electronicRepo.GetParameterByName("RoQui HTTP Server");
            var apiKey = electronicRepo.GetParameterByName("RoQui HTTP X-API-KEY");

            if (string.IsNullOrWhiteSpace(url?.Value) && string.IsNullOrWhiteSpace(apiKey?.Value))
            {
                throw new InvalidOperationException("Invalid configuration for electronic document authorization.");
            }

            var authorizeUrl = url.Value.TrimEnd('/') + documentUrl;

            await Task.Delay(6000);
            using var content = JsonContent.Create(new { code, number });
            using var request = new HttpRequestMessage(HttpMethod.Post, authorizeUrl)
            {
                Content = content
            };
            request.Headers.Add("X-API-KEY", apiKey.Value);
            var response = await HttpClient.SendAsync(request);
            Console.WriteLine($"Authorize invoice {code}-{number}: {(int)response.StatusCode}");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error authorizing invoice {code}-{number}: {ex.Message}");
        }
    }

    public static async Task Version(string versionUrl, string apiKey)
    {
        try
        {
            await Task.Delay(6000);
            using var request = new HttpRequestMessage(HttpMethod.Get, versionUrl);
            request.Headers.Add("X-API-KEY", apiKey);
            var response = await HttpClient.SendAsync(request);
            Console.WriteLine($"Version check: {(int)response.StatusCode}");
            // return await response.Content.ReadFromJsonAsync<VersionDto>();
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error checking version: {ex.Message}");
            // return null;
        }
    }
}