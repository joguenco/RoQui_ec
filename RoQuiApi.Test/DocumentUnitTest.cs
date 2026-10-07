namespace RoQuiApi.Test;

using Newtonsoft.Json;
using RoQuiApi.RoQui.Head.Dto;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Xunit;

public class DocumentUnitTest : IClassFixture<CustomWebApplicationFactory<Program>>
{
    private readonly HttpClient _client;
    private readonly string X_API_KEY = "api__6tpXYCxsXpCs7QeuI44KtoCq";

    public DocumentUnitTest(CustomWebApplicationFactory<Program> factory)
    {
        _client = factory.CreateClient();
    }

    [Fact]
    public async Task DocumentTest()
    {
        var taxpayer = new TaxpayerDto()
        {
            Identification = "1234567890123",
            LegalName = "Test Legal Name",
            ForcedAccounting = "SI",
            Establishments =
            [
                new EstablishmentDto
                {
                    Code = "001",
                    BusinessName = "Main Establishment",
                    Address = "123 Main St",
                    IsPrincipal = true
                },
                new EstablishmentDto
                {
                    Code = "002",
                    BusinessName = "Secondary Establishment",
                    Address = "456 Secondary St",
                    IsPrincipal = true
                }
            ]
        };

        var json = JsonConvert.SerializeObject(taxpayer, Formatting.Indented);
        Console.WriteLine($"Serialized TaxpayerDto: {json}");
        var httpContent = new StringContent(json, Encoding.UTF8, "application/json");
        using var request = new HttpRequestMessage(HttpMethod.Post, "/taxpayer/rest/v1/taxpayer")
        {
            Content = httpContent
        };
        request.Headers.Add("X-API-KEY", X_API_KEY);
        var apiResponse = await _client.SendAsync(request);

        Assert.True(apiResponse.IsSuccessStatusCode);

        var stringResponse = await apiResponse.Content.ReadAsStringAsync();
        Console.WriteLine($"Response: {stringResponse}");
        dynamic dynamicData = JsonConvert.DeserializeObject(stringResponse);
        bool success = dynamicData.title == "Taxpayer created successfully";
        Assert.True(success);
    }
}