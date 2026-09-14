namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using AutoMapper;
using RoQuiApi.RoQui.Invoice.Dto;
using RoQuiApi.RoQui.Invoice.Model;
using RoQuiApi.RoQui.Invoice.Repository;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Electronic.Client;
using RoQuiApi.RoQui.Shared;

[ApiController]
[Route("[controller]")]
public class LiquidationController : ControllerBase
{
    private readonly IInvoiceRepo _invoiceRepo;

    private readonly IElectronicRepo _electronicRepo;
    private readonly IMapper _mapper;

    public LiquidationController(IInvoiceRepo invoiceRepo, IElectronicRepo electronicRepo, IMapper mapper)
    {
        _invoiceRepo = invoiceRepo;
        _electronicRepo = electronicRepo;
        _mapper = mapper;
    }

    [HttpPost("rest/v1/liquidation", Name = "CreateLiquidation")]
    public ActionResult<MessageDto> CreateLiquidation(LiquidationDto liquidationBody)
    {
        var existingDocument = _invoiceRepo.GetDocumentByCodeAndNumber(liquidationBody.Code, liquidationBody.Number);
        if (existingDocument != null)
        {
            _invoiceRepo.DeleteDocument(existingDocument);
        }

        var liquidationModel = _mapper.Map<Document>(liquidationBody);
        var liquidationDetailsModel = _mapper.Map<List<DocumentDetail>>(liquidationBody.LiquidationDetails);
        liquidationModel.DocumentDetails = liquidationDetailsModel;
        // La liquidacion de compras no lleva formas de pago en el XML del SRI
        liquidationModel.Payments = [];
        _invoiceRepo.CreateInvoice(liquidationModel);
        _invoiceRepo.SaveChanges();

        _ = Client.Authorize("/roqui/v2/liquidation/authorize", liquidationBody.Code, liquidationBody.Number, _electronicRepo);

        return Ok(new MessageDto { Title = "Liquidation created successfully" });
    }
}
