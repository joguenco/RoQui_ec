namespace RoQuiApi.Test;

using Newtonsoft.Json;
using RoQuiApi.RoQui.Head.Dto;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Xunit;

[Collection("01_Taxpayer")]
public class TaxPayerUnitTest : IClassFixture<CustomWebApplicationFactory<Program>>
{
    private readonly HttpClient _client;

    public TaxPayerUnitTest(CustomWebApplicationFactory<Program> factory)
    {
        _client = factory.CreateClient();
    }

    public static TaxpayerDto InitData()
    {
        return new TaxpayerDto()
        {
            Identification = "0123456789001",
            LegalName = "Test Legal Name",
            ForcedAccounting = "SI",
            Establishments =
            [
                new EstablishmentDto
                {
                    Code = "001",
                    BusinessName = "Main Establishment",
                    Address = "123 Main St",
                    IsPrincipal = false
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

    public static TaxpayerDto SetGoodEstablishment(TaxpayerDto taxpayer)
    {
        foreach (var establishment in taxpayer.Establishments)
        {
            if (establishment == taxpayer.Establishments.First())
            {
                establishment.IsPrincipal = true; // Set the first establishment as principal
            }
            else
            {
                establishment.IsPrincipal = false; // Set the rest as non-principal
            }
        }
        return taxpayer;
    }

    private static async Task AssertInvalidTaxpayerResponseAsync(HttpResponseMessage apiResponse)
    {
        Assert.False(apiResponse.IsSuccessStatusCode);

        var stringResponse = await apiResponse.Content.ReadAsStringAsync();
        Console.WriteLine($"Response: {stringResponse}");
        dynamic dynamicData = JsonConvert.DeserializeObject(stringResponse);
        bool title = dynamicData.title == "Invalid data";
        Assert.True(title);
    }

    [Fact]
    public async Task TaxpayerTestWhenIsPrincipalAllIsFalse()
    {
        var taxpayer = InitData();

        Console.WriteLine($"TaxpayerTestWhenIsPrincipalAllIsFalse: {JsonConvert.SerializeObject(taxpayer)}");
        var apiResponse = await DataTest.SendTaxpayerAsync(_client, taxpayer);
        await AssertInvalidTaxpayerResponseAsync(apiResponse);
    }

    [Fact]
    public async Task TaxpayerTestWhenIsPrincipalAllIsTrue()
    {
        var taxpayer = InitData();

        foreach (var establishment in taxpayer.Establishments)
        {
            establishment.IsPrincipal = true;
        }

        Console.WriteLine($"TaxpayerTestWhenIsPrincipalAllIsTrue: {JsonConvert.SerializeObject(taxpayer)}");
        var apiResponse = await DataTest.SendTaxpayerAsync(_client, taxpayer);
        await AssertInvalidTaxpayerResponseAsync(apiResponse);
    }

    [Fact]
    public async Task TaxpayerTestWithSameEstablishmentCode()
    {
        var taxpayer = InitData();

        foreach (var establishment in taxpayer.Establishments)
        {
            establishment.Code = "001"; // Set the same code for all establishments
            if (establishment == taxpayer.Establishments.First())
            {
                establishment.IsPrincipal = true; // Set the first establishment as principal
            }
            else
            {
                establishment.IsPrincipal = false; // Set the rest as non-principal
            }
        }

        Console.WriteLine($"TaxpayerTestWithSameEstablishmentCode: {JsonConvert.SerializeObject(taxpayer)}");
        var apiResponse = await DataTest.SendTaxpayerAsync(_client, taxpayer);
        await AssertInvalidTaxpayerResponseAsync(apiResponse);
    }

    [Fact]
    public async Task TaxpayerTestWithoutEstablishments()
    {
        var taxpayer = InitData();
        taxpayer.Establishments.Clear(); // Remove all establishments

        Console.WriteLine($"TaxpayerTestWithoutEstablishments: {JsonConvert.SerializeObject(taxpayer)}");
        var apiResponse = await DataTest.SendTaxpayerAsync(_client, taxpayer);
        await AssertInvalidTaxpayerResponseAsync(apiResponse);
    }

    [Fact]
    public async Task TaxpayerTestWithBadIdentification()
    {
        var taxpayer = InitData();
        taxpayer.Identification = "123"; // Set a bad identification
        taxpayer = SetGoodEstablishment(taxpayer); // Ensure one establishment is principal        

        Console.WriteLine($"TaxpayerTestWithBadIdentification: {JsonConvert.SerializeObject(taxpayer)}");
        var apiResponse = await DataTest.SendTaxpayerAsync(_client, taxpayer);
        await AssertInvalidTaxpayerResponseAsync(apiResponse);
    }

    [Fact]
    public async Task TaxpayerTestWithBadForcedAccounting()
    {
        var taxpayer = InitData();
        taxpayer.ForcedAccounting = "MAYBE"; // Set a bad ForcedAccounting value
        taxpayer = SetGoodEstablishment(taxpayer); // Ensure one establishment is principal        

        Console.WriteLine($"TaxpayerTestWithBadForcedAccounting: {JsonConvert.SerializeObject(taxpayer)}");
        var apiResponse = await DataTest.SendTaxpayerAsync(_client, taxpayer);
        await AssertInvalidTaxpayerResponseAsync(apiResponse);
    }

    [Fact]
    public async Task TaxpayerTestWithBadSpecialTaxpayer()
    {
        var taxpayer = InitData();
        taxpayer.SpecialTaxpayer = "12"; // Set a bad SpecialTaxpayer value
        taxpayer = SetGoodEstablishment(taxpayer); // Ensure one establishment is principal        

        Console.WriteLine($"TaxpayerTestWithBadSpecialTaxpayer: {JsonConvert.SerializeObject(taxpayer)}");
        var apiResponse = await DataTest.SendTaxpayerAsync(_client, taxpayer);
        await AssertInvalidTaxpayerResponseAsync(apiResponse);
    }

    [Fact]
    public async Task TaxpayerTestWithBadRetentionAgent()
    {
        var taxpayer = InitData();
        taxpayer.RetentionAgent = "2"; // Set a bad RetentionAgent value
        taxpayer = SetGoodEstablishment(taxpayer); // Ensure one establishment is principal        

        Console.WriteLine($"TaxpayerTestWithBadRetentionAgent: {JsonConvert.SerializeObject(taxpayer)}");
        var apiResponse = await DataTest.SendTaxpayerAsync(_client, taxpayer);
        await AssertInvalidTaxpayerResponseAsync(apiResponse);
    }

    [Fact]
    public async Task TaxpayerTestWithBadRimpe()
    {
        var taxpayer = InitData();
        taxpayer.Rimpe = "INVALID"; // Set a bad Rimpe value
        taxpayer = SetGoodEstablishment(taxpayer); // Ensure one establishment is principal        

        Console.WriteLine($"TaxpayerTestWithBadRimpe: {JsonConvert.SerializeObject(taxpayer)}");
        var apiResponse = await DataTest.SendTaxpayerAsync(_client, taxpayer);
        await AssertInvalidTaxpayerResponseAsync(apiResponse);
    }

    [Fact]
    public async Task GoodTaxpayerTest()
    {
        var taxpayer = DataTest.InitData();

        Console.WriteLine($"GoodTaxpayerTest: {JsonConvert.SerializeObject(taxpayer)}");
        var apiResponse = await DataTest.SendTaxpayerAsync(_client, taxpayer);
        Assert.True(apiResponse.IsSuccessStatusCode);
    }
}