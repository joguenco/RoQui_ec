namespace RoQuiApi.Test;

using Newtonsoft.Json;
using RoQuiApi.RoQui.Head.Dto;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

public static class DataTest
{
    public const string X_API_KEY = "api__6tpXYCxsXpCs7QeuI44KtoCq";

    public static async Task<HttpResponseMessage> SendTaxpayerAsync(
        HttpClient client,
        TaxpayerDto taxpayer
    )
    {
        var taxpayerJson = JsonConvert.SerializeObject(taxpayer, Formatting.Indented);
        using var request = new HttpRequestMessage(HttpMethod.Post, "/taxpayer/rest/v1/taxpayer")
        {
            Content = new StringContent(taxpayerJson, Encoding.UTF8, "application/json")
        };
        request.Headers.Add("X-API-KEY", X_API_KEY);
        return await client.SendAsync(request);
    }

    public static TaxpayerDto InitData()
    {
        return new TaxpayerDto()
        {
            Identification = "0123456789001",
            LegalName = "Test Legal Name",
            ForcedAccounting = "SI",
            SpecialTaxpayer = "12345",
            RetentionAgent = "1",
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
                    IsPrincipal = false
                },
                new EstablishmentDto
                {
                    Code = "003",
                    BusinessName = "Tertiary Establishment",
                    Address = "789 Tertiary St",
                    IsPrincipal = false
                }
            ]
        };
    }
}