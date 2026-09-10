namespace RoQuiApi.RoQui.Invoice.Repository;

using RoQuiApi.RoQui.Invoice.Model;

public interface IWithholdRepo
{
    Withhold? GetWithholdByCodeAndNumber(string code, string number);

    void DeleteWithhold(Withhold withhold);
    void CreateWithhold(Withhold withhold);
    bool SaveChanges();
}
