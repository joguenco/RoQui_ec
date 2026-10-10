namespace RoQuiApi.Test;

using Newtonsoft.Json;
using System.Net.Http;
using System.Threading.Tasks;
using Xunit;

public class VersionUnitTest : IClassFixture<CustomWebApplicationFactory<Program>>
{
    private readonly HttpClient _client;

    public VersionUnitTest(CustomWebApplicationFactory<Program> factory)
    {
        _client = factory.CreateClient();
    }

    [Fact]
    public async Task VersionTest()
    {
        var apiResponse = await _client.GetAsync("/version");
        Assert.True(apiResponse.IsSuccessStatusCode);

        var stringResponse = await apiResponse.Content.ReadAsStringAsync();
        Console.WriteLine($"Version Response: {stringResponse}");
        dynamic dynamicData = JsonConvert.DeserializeObject(stringResponse);
        bool success = dynamicData.name == "RoQui API";
        Assert.True(success);
    }
}