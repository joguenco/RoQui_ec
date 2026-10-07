namespace RoQuiApi.Test;

using Newtonsoft.Json;
using System.Net.Http;
using System.Threading.Tasks;
using Xunit;

public class PingUnitTest : IClassFixture<CustomWebApplicationFactory<Program>>
{
    private readonly HttpClient _client;

    public PingUnitTest(CustomWebApplicationFactory<Program> factory)
    {
        _client = factory.CreateClient();
    }

    [Fact]
    public async Task PingTest()
    {

        var apiResponse = await _client.GetAsync("/ping");

        Assert.True(apiResponse.IsSuccessStatusCode);

        var stringResponse = await apiResponse.Content.ReadAsStringAsync();
        Console.WriteLine($"Ping Response: {stringResponse}");
        dynamic dynamicData = JsonConvert.DeserializeObject(stringResponse);
        bool success = dynamicData.message == "Pong";
        Assert.True(success);
    }
}