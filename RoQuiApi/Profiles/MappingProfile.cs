using AutoMapper;
using RoQuiApi.RoQui.Head.Dto;
using RoQuiApi.RoQui.Head.Model;
using RoQuiApi.RoQui.Invoice.Dto;
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
        CreateMap<InvoiceDetail, InvoiceDetailDto>();
        CreateMap<InvoiceDetailDto, InvoiceDetail>();
        CreateMap<InvoiceDetailTax, TaxDto>();
        CreateMap<TaxDto, InvoiceDetailTax>();
    }
}
