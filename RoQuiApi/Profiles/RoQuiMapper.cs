using Riok.Mapperly.Abstractions;
using RoQuiApi.RoQui.CreditNote.Dto;
using RoQuiApi.RoQui.Document.DeliveryNote.Dto;
using RoQuiApi.RoQui.Document.Dto;
using RoQuiApi.RoQui.Document.Invoice.Dto;
using RoQuiApi.RoQui.Document.Model;
using RoQuiApi.RoQui.Document.Withhold.Dto;
using RoQuiApi.RoQui.Head.Dto;
using RoQuiApi.RoQui.Head.Model;

namespace RoQuiApi.Profiles;

[Mapper]
public partial class RoQuiMapper
{
    // Taxpayer
    public partial TaxpayerDto ToTaxpayerDto(Taxpayer taxpayer);

    public partial Taxpayer ToTaxpayer(TaxpayerDto dto);

    public partial EstablishmentDto ToEstablishmentDto(Establishment establishment);

    [MapValue(nameof(Establishment.Taxpayer), Use = nameof(NullTaxpayer))]
    public partial Establishment ToEstablishment(EstablishmentDto dto);

    public partial ICollection<Establishment> ToEstablishments(ICollection<EstablishmentDto> dtos);

    // Invoice
    [MapProperty(nameof(Document.DocumentDetails), nameof(InvoiceDto.InvoiceDetails))]
    [MapProperty(nameof(Document.DocumentPayments), nameof(InvoiceDto.Payments))]
    [MapProperty(nameof(Document.DocumentInformations), nameof(InvoiceDto.Informations))]
    public partial InvoiceDto ToInvoiceDto(Document document);

    [MapProperty(nameof(InvoiceDto.InvoiceDetails), nameof(Document.DocumentDetails))]
    [MapProperty(nameof(InvoiceDto.Payments), nameof(Document.DocumentPayments))]
    [MapProperty(nameof(InvoiceDto.Informations), nameof(Document.DocumentInformations))]
    public partial Document ToInvoiceDocument(InvoiceDto dto);

    public partial InvoiceDetailDto ToInvoiceDetailDto(DocumentDetail detail);

    [MapValue(nameof(DocumentDetail.Document), Use = nameof(NullDocument))]
    public partial DocumentDetail ToInvoiceDocumentDetail(InvoiceDetailDto dto);

    public partial List<DocumentDetail> ToInvoiceDocumentDetails(ICollection<InvoiceDetailDto> dtos);

    public partial TaxDto ToTaxDto(DocumentDetailTax tax);

    [MapValue(nameof(DocumentDetailTax.DocumentDetail), Use = nameof(NullDocumentDetail))]
    public partial DocumentDetailTax ToDocumentDetailTax(TaxDto dto);

    public partial PaymentDto ToPaymentDto(DocumentPayment payment);

    [MapValue(nameof(DocumentPayment.Document), Use = nameof(NullDocument))]
    public partial DocumentPayment ToDocumentPayment(PaymentDto dto);

    public partial List<DocumentPayment> ToDocumentPayments(ICollection<PaymentDto> dtos);

    // Liquidation
    [MapProperty(nameof(Document.DocumentDetails), nameof(LiquidationDto.LiquidationDetails))]
    public partial LiquidationDto ToLiquidationDto(Document document);

    [MapProperty(nameof(LiquidationDto.LiquidationDetails), nameof(Document.DocumentDetails))]
    [MapValue(nameof(Document.DocumentPayments), Use = nameof(EmptyDocumentPayments))]
    [MapValue(nameof(Document.DocumentInformations), Use = nameof(EmptyDocumentInformations))]
    public partial Document ToLiquidationDocument(LiquidationDto dto);

    // El detalle de liquidacion llama a sus impuestos LiquidationDetailTaxes,
    // pero en el modelo la coleccion se llama InvoiceDetailTaxes
    [MapProperty(nameof(DocumentDetail.InvoiceDetailTaxes), nameof(LiquidationDetailDto.LiquidationDetailTaxes))]
    public partial LiquidationDetailDto ToLiquidationDetailDto(DocumentDetail detail);

    [MapProperty(nameof(LiquidationDetailDto.LiquidationDetailTaxes), nameof(DocumentDetail.InvoiceDetailTaxes))]
    [MapValue(nameof(DocumentDetail.Document), Use = nameof(NullDocument))]
    public partial DocumentDetail ToLiquidationDocumentDetail(LiquidationDetailDto dto);

    public partial List<DocumentDetail> ToLiquidationDocumentDetails(ICollection<LiquidationDetailDto> dtos);

    // Informations
    public partial InformationDto ToInformationDto(DocumentInformation information);

    [MapValue(nameof(DocumentInformation.Document), Use = nameof(NullDocument))]
    public partial DocumentInformation ToDocumentInformation(InformationDto dto);

    public partial List<DocumentInformation> ToDocumentInformations(ICollection<InformationDto>? dtos);

    // Credit note
    // La nota de credito llama "Modified*" a lo que el modelo guarda como "Updated*"
    [MapProperty(nameof(Document.UpdatedCodeDocument), nameof(CreditNoteDto.ModifiedDocumentType))]
    [MapProperty(nameof(Document.UpdatedNumberDocument), nameof(CreditNoteDto.ModifiedDocument))]
    [MapProperty(nameof(Document.UpdatedDateDocument), nameof(CreditNoteDto.ModifiedDate))]
    [MapProperty(nameof(Document.DocumentDetails), nameof(CreditNoteDto.CreditNoteDetails))]
    [MapValue(nameof(CreditNoteDto.TotalWithoutTaxes), Use = nameof(ZeroDecimal))]
    [MapValue(nameof(CreditNoteDto.ModifiedTotal), Use = nameof(ZeroDecimal))]
    public partial CreditNoteDto ToCreditNoteDto(Document document);

    [MapProperty(nameof(CreditNoteDto.ModifiedDocumentType), nameof(Document.UpdatedCodeDocument))]
    [MapProperty(nameof(CreditNoteDto.ModifiedDocument), nameof(Document.UpdatedNumberDocument))]
    [MapProperty(nameof(CreditNoteDto.ModifiedDate), nameof(Document.UpdatedDateDocument))]
    [MapProperty(nameof(CreditNoteDto.CreditNoteDetails), nameof(Document.DocumentDetails))]
    [MapValue(nameof(Document.Address), Use = nameof(EmptyString))]
    [MapValue(nameof(Document.DocumentPayments), Use = nameof(EmptyDocumentPayments))]
    [MapValue(nameof(Document.DocumentInformations), Use = nameof(EmptyDocumentInformations))]
    public partial Document ToCreditNoteDocument(CreditNoteDto dto);

    [MapProperty(nameof(DocumentDetail.InvoiceDetailTaxes), nameof(CreditNoteDetailDto.CreditNoteDetailTaxes))]
    public partial CreditNoteDetailDto ToCreditNoteDetailDto(DocumentDetail detail);

    [MapProperty(nameof(CreditNoteDetailDto.CreditNoteDetailTaxes), nameof(DocumentDetail.InvoiceDetailTaxes))]
    [MapValue(nameof(DocumentDetail.Document), Use = nameof(NullDocument))]
    public partial DocumentDetail ToCreditNoteDocumentDetail(CreditNoteDetailDto dto);

    public partial List<DocumentDetail> ToCreditNoteDocumentDetails(ICollection<CreditNoteDetailDto> dtos);

    // Withhold
    public partial WithholdDto ToWithholdDto(Withhold withhold);

    public partial Withhold ToWithhold(WithholdDto dto);

    public partial WithholdSupportDto ToWithholdSupportDto(WithholdSupport support);

    [MapValue(nameof(WithholdSupport.Withhold), Use = nameof(NullWithhold))]
    public partial WithholdSupport ToWithholdSupport(WithholdSupportDto dto);

    public partial WithholdDetailDto ToWithholdDetailDto(WithholdDetail detail);

    [MapValue(nameof(WithholdDetail.WithholdSupport), Use = nameof(NullWithholdSupport))]
    public partial WithholdDetail ToWithholdDetail(WithholdDetailDto dto);

    public partial WithholdDocumentTaxDto ToWithholdDocumentTaxDto(WithholdDocumentTax tax);

    [MapValue(nameof(WithholdDocumentTax.WithholdSupport), Use = nameof(NullWithholdSupport))]
    public partial WithholdDocumentTax ToWithholdDocumentTax(WithholdDocumentTaxDto dto);

    public partial List<WithholdSupport> ToWithholdSupports(ICollection<WithholdSupportDto> dtos);

    // Delivery note
    public partial DeliveryNoteDto ToDeliveryNoteDto(DeliveryNote deliveryNote);

    public partial DeliveryNote ToDeliveryNote(DeliveryNoteDto dto);

    public partial DeliveryNoteReceiverDto ToDeliveryNoteReceiverDto(DeliveryNoteReceiver receiver);

    [MapValue(nameof(DeliveryNoteReceiver.DeliveryNote), Use = nameof(NullDeliveryNote))]
    public partial DeliveryNoteReceiver ToDeliveryNoteReceiver(DeliveryNoteReceiverDto dto);

    public partial List<DeliveryNoteReceiver> ToDeliveryNoteReceivers(ICollection<DeliveryNoteReceiverDto> dtos);

    public partial DeliveryNoteReceiverDetailDto ToDeliveryNoteReceiverDetailDto(DeliveryNoteReceiverDetail detail);

    [MapValue(nameof(DeliveryNoteReceiverDetail.DeliveryNoteReceiver), Use = nameof(NullDeliveryNoteReceiver))]
    public partial DeliveryNoteReceiverDetail ToDeliveryNoteReceiverDetail(DeliveryNoteReceiverDetailDto dto);

    // Debit note
    // La nota de debito usa los mismos Modified* que la de credito
    [MapProperty(nameof(DebitNoteDto.ModifiedDocumentType), nameof(Document.UpdatedCodeDocument))]
    [MapProperty(nameof(DebitNoteDto.ModifiedDocument), nameof(Document.UpdatedNumberDocument))]
    [MapProperty(nameof(DebitNoteDto.ModifiedDate), nameof(Document.UpdatedDateDocument))]
    [MapValue(nameof(Document.Address), Use = nameof(EmptyString))]
    [MapValue(nameof(Document.DocumentDetails), Use = nameof(EmptyDocumentDetails))]
    [MapValue(nameof(Document.DocumentPayments), Use = nameof(EmptyDocumentPayments))]
    [MapValue(nameof(Document.DocumentInformations), Use = nameof(EmptyDocumentInformations))]
    public partial Document ToDebitNoteDocument(DebitNoteDto dto);

    [MapValue(nameof(DebitNoteReason.Line), Use = nameof(Zero))]
    public partial DebitNoteReason ToDebitNoteReason(DebitNoteDetailDto dto);

    public partial DebitNoteTax ToDebitNoteTax(TaxDto dto);

    public partial List<DebitNoteTax> ToDebitNoteTaxes(ICollection<TaxDto> dtos);

    // Navegaciones que llena EF Core, no el mapper.
    private static Document NullDocument() => null!;

    private static DocumentDetail NullDocumentDetail() => null!;

    private static Taxpayer NullTaxpayer() => null!;

    private static Withhold NullWithhold() => null!;

    private static WithholdSupport NullWithholdSupport() => null!;

    private static DeliveryNote NullDeliveryNote() => null!;

    private static DeliveryNoteReceiver NullDeliveryNoteReceiver() => null!;

    private static ICollection<DocumentDetail> EmptyDocumentDetails() => [];

    private static ICollection<DocumentPayment> EmptyDocumentPayments() => [];

    private static ICollection<DocumentInformation> EmptyDocumentInformations() => [];

    private static string EmptyString() => "";

    private static int Zero() => 0;

    private static decimal ZeroDecimal() => 0m;
}
