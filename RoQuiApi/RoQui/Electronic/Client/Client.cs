namespace RoQuiApi.RoQui.Electronic.Client;

using System.Net.Http.Json;
public static class Client
{
    private static readonly HttpClient HttpClient = new();

    public static async Task AuthorizeInvoice(string authorizeUrl, string apiKey, string code, string number)
    {
        try
        {
            await Task.Delay(6000);
            using var content = JsonContent.Create(new { code, number });
            using var request = new HttpRequestMessage(HttpMethod.Post, authorizeUrl)
            {
                Content = content
            };
            request.Headers.Add("X-API-KEY", apiKey);
            var response = await HttpClient.SendAsync(request);
            Console.WriteLine($"Authorize invoice {code}-{number}: {(int)response.StatusCode}");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error authorizing invoice {code}-{number}: {ex.Message}");
        }
    }

    public static async Task<VersionDto?> Version(string versionUrl, string apiKey)
    {
        try
        {
            using var request = new HttpRequestMessage(HttpMethod.Get, versionUrl);
            request.Headers.Add("X-API-KEY", apiKey);
            var response = await HttpClient.SendAsync(request);
            Console.WriteLine($"Version check: {(int)response.StatusCode}");
            return await response.Content.ReadFromJsonAsync<VersionDto>();
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error checking version: {ex.Message}");
            return null;
        }
    }
}