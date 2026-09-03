namespace RoQuiApi.RoQui.Invoice.Repository;

using RoQuiApi.RoQui.Invoice.Model;

public interface IInvoiceRepo
{
    void CreateInvoice(Invoice invoice);
    bool SaveChanges();
}