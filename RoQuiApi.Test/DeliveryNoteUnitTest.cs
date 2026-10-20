namespace RoQuiApi.Test;

using Newtonsoft.Json;
using RoQuiApi.RoQui.Document.DeliveryNote.Dto;
using RoQuiApi.RoQui.Shared;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Xunit;

[Collection("07_Delivery_Note")]
[TestCaseOrderer(
    ordererTypeName: "RoQuiApi.Test.MethodNameOrderer",
    ordererAssemblyName: "RoQuiApi.Test")]
public class DeliveryNoteUnitTest : IClassFixture<CustomWebApplicationFactory<Program>>
{

    private readonly HttpClient _client;

    public DeliveryNoteUnitTest(CustomWebApplicationFactory<Program> factory)
    {
        _client = factory.CreateClient();
    }

    public static DeliveryNoteDto InitDeliveryNoteData()
    {
        return new DeliveryNoteDto
        {
            Code = "GUI",
            Number = "001002000002179",
            Date = new DateTime(2026, 10, 20, 0, 0, 0, DateTimeKind.Utc),
            AddressStart = "SUCUA 1-20 Y GENERAL URDANETA",
            CarrierIdentificationType = "05",
            CarrierIdentification = "0501797559",
            CarrierLegalName = "EDISON",
            Plate = "PBI-0001",
            DateStartTransport = new DateTime(2026, 10, 20, 0, 0, 0, DateTimeKind.Utc),
            DateEndTransport = new DateTime(2026, 10, 25, 0, 0, 0, DateTimeKind.Utc),
            AccessKey = "2010202606012345678900110010020000021791234567819",
            DeliveryNoteReceivers =
            [
                new()
                {
                    Line = 1,
                    IdentificationType = "04",
                    Identification = "1760013210001",
                    LegalName = "Servicio de Rentas Internas",
                    Address = "Quito",
                    TransferReason = "Venta de productos",
                    CodeDocumentSupport = "01",
                    NumberDocumentSupport = "001-003-003016875",
                    DateDocumentSupport = new DateTime(2026, 10, 19, 16, 13, 2, DateTimeKind.Utc),
                    DeliveryNoteReceiverDetails =
                    [
                        new() { Line = 1, PrincipalCode = "4370", Name = "Producto tarifa diferente de 0%", Quantity = 3 },
                        new() { Line = 2, PrincipalCode = "4371", Name = "Producto tarifa 0%", Quantity = 3 }
                    ]
                },
                new()
                {
                    Line = 2,
                    IdentificationType = "04",
                    Identification = "1004718290001",
                    LegalName = "JIMMY ERNESTO",
                    Address = "VACAS 2-21",
                    TransferReason = "Venta de productos",
                    CodeDocumentSupport = "01",
                    NumberDocumentSupport = "001-004-000002932",
                    DateDocumentSupport = new DateTime(2026, 10, 16, 17, 19, 27, DateTimeKind.Utc),
                    DeliveryNoteReceiverDetails =
                    [
                        new() { Line = 1, PrincipalCode = "1298", Name = "LUBRISTONE REFRIGEREANTE 50 GL.", Quantity = 1 }
                    ]
                },
                new()
                {
                    Line = 3,
                    IdentificationType = "04",
                    Identification = "1002376444001",
                    LegalName = "SIXTO VINICIO",
                    Address = "COTACACHI",
                    TransferReason = "Venta de productos",
                    CodeDocumentSupport = "01",
                    NumberDocumentSupport = "001-004-000002931",
                    DateDocumentSupport = new DateTime(2026, 10, 16, 13, 13, 3, DateTimeKind.Utc),
                    DeliveryNoteReceiverDetails =
                    [
                        new() { Line = 1, PrincipalCode = "4318", Name = "GOLDEN CROWN LLANTA", Quantity = 2 }
                    ]
                }
            ]
        };
    }

    private async Task<HttpResponseMessage> SendDeliveryNoteAsync(DeliveryNoteDto DeliveryNote)
    {
        var DeliveryNoteJson = JsonConvert.SerializeObject(DeliveryNote, Formatting.Indented);
        using var request = new HttpRequestMessage(HttpMethod.Post, "/deliverynote/rest/v1/send")
        {
            Content = new StringContent(DeliveryNoteJson, Encoding.UTF8, "application/json")
        };
        request.Headers.Add("X-API-KEY", DataTest.X_API_KEY);
        return await _client.SendAsync(request);
    }

    [Fact]
    public async Task ACreateTaxpayerTest()
    {
        var taxpayer = DataTest.InitData();

        Console.WriteLine($"GoodTaxpayerTest: {JsonConvert.SerializeObject(taxpayer)}");
        var apiResponse = await DataTest.SendTaxpayerAsync(_client, taxpayer);
        Assert.True(apiResponse.IsSuccessStatusCode);
    }

    [Fact]
    public async Task GoodDeliveryNoteTest()
    {
        var deliveryNote = InitDeliveryNoteData();

        Console.WriteLine($"GoodDeliveryNoteTest: {JsonConvert.SerializeObject(deliveryNote)}");
        var DeliveryNoteResponse = await SendDeliveryNoteAsync(deliveryNote);

        var message = JsonConvert.DeserializeObject<MessageDto>(
            await DeliveryNoteResponse.Content.ReadAsStringAsync()
        );
        Assert.True(DeliveryNoteResponse.IsSuccessStatusCode);
    }
}