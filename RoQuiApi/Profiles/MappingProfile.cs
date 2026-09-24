using AutoMapper;
using RoQuiApi.RoQui.Document.Dto;
using RoQuiApi.RoQui.Document.Invoice.Dto;
using RoQuiApi.RoQui.Document.Withhold.Dto;
using RoQuiApi.RoQui.Document.DeliveryNote.Dto;
using RoQuiApi.RoQui.Invoice.Dto;
using RoQuiApi.RoQui.Head.Dto;
using RoQuiApi.RoQui.Head.Model;

using RoQuiApi.RoQui.Invoice.Model;

namespace RoQuiApi.Profiles;

public class MappingProfile : Profile
{
    public MappingProfile()
    {
        CreateMap<Taxpayer, TaxpayerDto>();
        CreateMap<TaxpayerDto, Taxpayer>();
        CreateMap<Establishment, EstablishmentDto>();
        CreateMap<EstablishmentDto, Establishment>();
        CreateMap<Document, InvoiceDto>();
        CreateMap<InvoiceDto, Document>();
        CreateMap<DocumentDetail, InvoiceDetailDto>();
        CreateMap<InvoiceDetailDto, DocumentDetail>();
        CreateMap<DocumentDetailTax, TaxDto>();
        CreateMap<TaxDto, DocumentDetailTax>();
        CreateMap<DocumentPayment, PaymentDto>();
        CreateMap<PaymentDto, DocumentPayment>();
        CreateMap<Document, LiquidationDto>();
        CreateMap<DocumentInformation, InformationDto>();
        CreateMap<InformationDto, DocumentInformation>();
        CreateMap<LiquidationDto, Document>();
        // El detalle de liquidacion llama a sus impuestos LiquidationDetailTaxes,
        // pero en el modelo la coleccion se llama InvoiceDetailTaxes
        CreateMap<DocumentDetail, LiquidationDetailDto>()
            .ForMember(dto => dto.LiquidationDetailTaxes, opt => opt.MapFrom(model => model.InvoiceDetailTaxes));
        CreateMap<LiquidationDetailDto, DocumentDetail>()
            .ForMember(model => model.InvoiceDetailTaxes, opt => opt.MapFrom(dto => dto.LiquidationDetailTaxes));
        // La nota de credito llama "Modified*" a lo que el modelo guarda como "Updated*"
        CreateMap<Document, CreditNoteDto>()
            .ForMember(dto => dto.ModifiedDocumentType, opt => opt.MapFrom(model => model.UpdatedCodeDocument))
            .ForMember(dto => dto.ModifiedDocument, opt => opt.MapFrom(model => model.UpdatedNumberDocument))
            .ForMember(dto => dto.ModifiedDate, opt => opt.MapFrom(model => model.UpdatedDateDocument));
        CreateMap<CreditNoteDto, Document>()
            .ForMember(model => model.UpdatedCodeDocument, opt => opt.MapFrom(dto => dto.ModifiedDocumentType))
            .ForMember(model => model.UpdatedNumberDocument, opt => opt.MapFrom(dto => dto.ModifiedDocument))
            .ForMember(model => model.UpdatedDateDocument, opt => opt.MapFrom(dto => dto.ModifiedDate));
        // Igual que en liquidacion, la coleccion de impuestos cambia de nombre entre dto y modelo
        CreateMap<DocumentDetail, CreditNoteDetailDto>()
            .ForMember(dto => dto.CreditNoteDetailTaxes, opt => opt.MapFrom(model => model.InvoiceDetailTaxes));
        CreateMap<CreditNoteDetailDto, DocumentDetail>()
            .ForMember(model => model.InvoiceDetailTaxes, opt => opt.MapFrom(dto => dto.CreditNoteDetailTaxes));
        CreateMap<Withhold, WithholdDto>();
        CreateMap<WithholdDto, Withhold>();
        CreateMap<WithholdSupport, WithholdSupportDto>();
        CreateMap<WithholdSupportDto, WithholdSupport>();
        CreateMap<WithholdDetail, WithholdDetailDto>();
        CreateMap<WithholdDetailDto, WithholdDetail>();
        CreateMap<WithholdDocumentTax, WithholdDocumentTaxDto>();
        CreateMap<WithholdDocumentTaxDto, WithholdDocumentTax>();
        CreateMap<DeliveryNote, DeliveryNoteDto>();
        CreateMap<DeliveryNoteDto, DeliveryNote>();
        CreateMap<DeliveryNoteReceiver, DeliveryNoteReceiverDto>();
        CreateMap<DeliveryNoteReceiverDto, DeliveryNoteReceiver>();
        CreateMap<DeliveryNoteReceiverDetail, DeliveryNoteReceiverDetailDto>();
        CreateMap<DeliveryNoteReceiverDetailDto, DeliveryNoteReceiverDetail>();
        // La nota de debito usa los mismos Modified* que la de credito
        CreateMap<DebitNoteDto, Document>()
            .ForMember(model => model.UpdatedCodeDocument, opt => opt.MapFrom(dto => dto.ModifiedDocumentType))
            .ForMember(model => model.UpdatedNumberDocument, opt => opt.MapFrom(dto => dto.ModifiedDocument))
            .ForMember(model => model.UpdatedDateDocument, opt => opt.MapFrom(dto => dto.ModifiedDate));
        CreateMap<DebitNoteDetailDto, DebitNoteReason>();
        CreateMap<TaxDto, DebitNoteTax>();
    }
}
