namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using AutoMapper;
using RoQuiApi.RoQui.Invoice.Dto;
using RoQuiApi.RoQui.Invoice.Model;
using RoQuiApi.RoQui.Invoice.Repository;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Electronic.Client;
using RoQuiApi.RoQui.Shared;
using RoQuiApi.RoQui.Security;
using System.Text.Json;

[ApiController]
[Route("[controller]")]
public class LiquidationController : ControllerBase
{
    private readonly IInvoiceRepo _invoiceRepo;
    private readonly IElectronicRepo _electronicRepo;
    private readonly IMapper _mapper;
    private readonly ILogger<LiquidationController> _logger;

    public LiquidationController(IInvoiceRepo invoiceRepo, IElectronicRepo electronicRepo, IMapper mapper, ILogger<LiquidationController> logger)
    {
        _invoiceRepo = invoiceRepo;
        _electronicRepo = electronicRepo;
        _mapper = mapper;
        _logger = logger;
    }

    [ApiKey]
    [HttpPost("rest/v1/liquidation/send", Name = "CreateLiquidation")]
    public async Task<ActionResult<MessageDto>> CreateLiquidation(LiquidationDto liquidationBody)
    {
        try
        {
            var electronic = _electronicRepo.GetElectronicByCodeAndNumber(liquidationBody.Code, liquidationBody.Number);
            if (electronic?.Status == "AUTORIZADO")
            {
                return Ok(new MessageDto { Title = electronic.Status });
            }

            var existingDocument = _invoiceRepo.GetDocumentByCodeAndNumber(liquidationBody.Code, liquidationBody.Number);
            if (existingDocument != null)
            {
                _invoiceRepo.DeleteDocument(existingDocument);
            }

            var liquidationModel = _mapper.Map<Document>(liquidationBody);
            var liquidationDetailsModel = _mapper.Map<List<DocumentDetail>>(liquidationBody.LiquidationDetails);
            liquidationModel.DocumentDetails = liquidationDetailsModel;
            // La liquidacion de compras no lleva formas de pago en el XML del SRI
            liquidationModel.DocumentPayments = [];
            _invoiceRepo.CreateInvoice(liquidationModel);
            _invoiceRepo.SaveChanges();

            _ = Client<LiquidationController>.Authorize("/roqui/v2/liquidation/authorize", liquidationBody.Code, liquidationBody.Number, _electronicRepo, _logger);

            _logger.LogInformation("Created {code} {number}", liquidationBody.Code, liquidationBody.Number);
            return Ok(new MessageDto { Title = "ENVIADO" });
        }
        catch (Exception ex)
        {
            _logger.LogCritical(ex, "Error: {liquidationBody}", JsonSerializer.Serialize(liquidationBody));
            return StatusCode(500, new MessageDto { Title = "Error", Errors = new Error { Message = [ex.Message] } });
        }
    }
}
