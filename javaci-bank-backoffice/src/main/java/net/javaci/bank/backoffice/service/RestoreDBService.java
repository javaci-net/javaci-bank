package net.javaci.bank.backoffice.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import net.javaci.bank.db.dao.AccountDao;
import net.javaci.bank.db.dao.CustomerDao;
import net.javaci.bank.db.dao.EmployeeDao;
import net.javaci.bank.db.model.Account;
import net.javaci.bank.db.model.Customer;
import net.javaci.bank.db.model.Employee;
import net.javaci.bank.db.model.UserEntityBase;
import net.javaci.bank.db.model.enumeration.AccountCurrency;
import net.javaci.bank.db.model.enumeration.AccountStatusType;
import net.javaci.bank.db.model.enumeration.CustomerStatusType;
import net.javaci.bank.db.model.enumeration.EmployeeRoleType;
import net.javaci.bank.db.model.enumeration.EmployeeStatusType;

@Service
public class RestoreDBService {

	private final static int RECORD_SIZE = 10;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public void checkEmptyDB() {
		Employee admin = employeeDao.findByEmail("admin");
		if (admin == null) {
			initDB();
		}
	}

	private void initDB() {
		int citizenNumber = 123456789;

		employeeDao.save(createAdmin(citizenNumber));

		for (int i = 0; i < RECORD_SIZE; i++) {
			employeeDao.save(createDummyEmployee(++citizenNumber));
		}

		for (int i = 0; i < RECORD_SIZE; i++) {
			Customer dummyCustomer = createDummyCustomer(++citizenNumber);
			customerDao.save(dummyCustomer);
			for (AccountCurrency currency : AccountCurrency.values()) {
				accountDao.save(createDummyAccount(dummyCustomer, currency));
			}
		}
	}

	private Employee createAdmin(int citizenNumber) {
		Employee admin = new Employee();
		admin.setEmail("admin");
		admin.setFirstName("Spring");
		admin.setLastName("Java");
		admin.setPassword(passwordEncoder.encode("admin"));
		admin.setCitizenNumber(citizenNumber + "");
		admin.setBirthDate(LocalDate.of(1980, 1, 1));
		admin.setStatus(EmployeeStatusType.ACTIVE);
		admin.setRole(EmployeeRoleType.ADMIN);
		return admin;
	}

	private Employee createDummyEmployee(int citizenNumber) {
		Employee dummy = new Employee();
		createUserBase(citizenNumber, dummy);
		dummy.setStatus(EmployeeStatusType.ACTIVE);
		dummy.setRole(EmployeeRoleType.USER);
		return dummy;
	}

	private Customer createDummyCustomer(int citizenNumber) {
		Customer dummy = new Customer();
		createUserBase(citizenNumber, dummy);
		dummy.setStatus(CustomerStatusType.ACTIVE);
		return dummy;
	}

	private void createUserBase(int citizenNumber, UserEntityBase dummy) {
		dummy.setFirstName(generateName());
		if (Math.random() < 0.3)
			dummy.setMiddleName(generateName());
		dummy.setLastName(generateLastName());
		dummy.setEmail(generateEmail(dummy));
		dummy.setPassword(passwordEncoder.encode(generatePassword()));
		dummy.setCitizenNumber(citizenNumber + "");
		dummy.setBirthDate(generateBirthDate());
	}

	private Account createDummyAccount(Customer customer, AccountCurrency currency) {
		Account dummy = new Account();
		dummy.setCustomer(customer);
		dummy.setCurrency(currency);
		dummy.setAccountNumber(customer.getCitizenNumber() + "-" + currency.name());
		dummy.setAccountName(currency.name() + "_account");
		dummy.setDescription("Benim " + currency.name() + " hesabim");
		dummy.setBalance(new BigDecimal(1_000_000));
		dummy.setStatus(AccountStatusType.ACTIVE);
		return dummy;
	}

	private String generateName() {
		return convertNameFormat(NAME_LIST.get(random.nextInt(NAME_LIST.size())));
	}
	
	private String generateLastName() {
		return convertNameFormat(SURNAME_LIST.get(random.nextInt(SURNAME_LIST.size())));
	}

	private String generateEmail(UserEntityBase user) {
		String email = "";
		
		boolean withDot = random.nextBoolean();
		
		
		if (random.nextBoolean()) {
			// without sort name
			if (user.getMiddleName() == null || random.nextBoolean()) {
				email = convertToEnglish(toLower(user.getFirstName())) + (withDot ? "." : "") + convertToEnglish(toLower(user.getLastName()));
			} else {
				email = convertToEnglish(toLower(user.getMiddleName())) + (withDot ? "." : "") + convertToEnglish(toLower(user.getLastName()));
			}
		} else {
			// with short name
			if (random.nextBoolean()) {
				// first name short
				if (user.getMiddleName() == null || random.nextBoolean()) {
					email = convertToEnglish(toLower(user.getFirstName().substring(0, 1))) + (withDot ? "." : "") + convertToEnglish(toLower(user.getLastName()));
				} else {
					email = convertToEnglish(toLower(user.getMiddleName().substring(0, 1))) + (withDot ? "." : "") + convertToEnglish(toLower(user.getLastName()));
				}
			} else {
				// last name short
				if (user.getMiddleName() == null || random.nextBoolean()) {
					email = convertToEnglish(toLower(user.getFirstName())) + (withDot ? "." : "") + convertToEnglish(toLower(user.getLastName().substring(0, 1)));
				} else {
					email = convertToEnglish(toLower(user.getMiddleName())) + (withDot ? "." : "") + convertToEnglish(toLower(user.getLastName().substring(0, 1)));
				}
			}
		}
		
		return email + "@javaci.net";
	}

	private String generatePassword() {
		return new String[] {"java", "javaci", "javacinet", "javacibank"}[random.nextInt(4)];
	}

	private LocalDate generateBirthDate() {
		return LocalDate.of(random.nextInt(50) + 1918, random.nextInt(12) + 1, random.nextInt(28) + 1);
	}
	
	private String convertNameFormat(String name) {
		return name.substring(0, 1) + toLower(name.substring(1));
	}
	
	private String toLower(String name) {
		return name.toLowerCase(new Locale("tr", "TR"));
	}
	
	private String convertToEnglish(String name) {
		return name
				.replaceAll("İ", "I")
				.replaceAll("Ö", "O")
				.replaceAll("Ü", "U")
				.replaceAll("Ğ", "G")
				.replaceAll("Ş", "S")
				.replaceAll("Ç", "C")
				.replaceAll("ö", "o")
				.replaceAll("ü", "u")
				.replaceAll("ğ", "g")
				.replaceAll("ş", "s")
				.replaceAll("ç", "c");
	}
	
	private static Random random = new Random();

	private static final List<String> NAME_LIST = Arrays.asList(new String[] {"JALE", "ALİ", "MAHMUT", "MANSUR KÜRŞAD",
			"GAMZE", "MİRAÇ", "YÜCEL", "KUBİLAY", "HAYATİ", "BEDRİYE MÜGE", "BİRSEN", "SERDAL", "BÜNYAMİN", "ÖZGÜR",
			"FERDİ", "REYHAN", "İLHAN", "GÜLŞAH", "NALAN", "SEMİH", "ERGÜN", "FATİH", "ŞENAY", "SERKAN", "EMRE",
			"BAHATTİN", "IRAZCA", "HATİCE", "BARIŞ", "REZAN", "FATİH", "FUAT", "GÖKHAN", "ORHAN", "MEHMET", "EVREN",
			"OKTAY", "HARUN", "YAVUZ", "PINAR", "MEHMET", "UMUT", "MESUDE", "HÜSEYİN CAHİT", "HAŞİM ONUR",
			"EYYUP SABRİ", "MUSTAFA", "MUSTAFA", "UFUK", "AHMET ALİ", "MEDİHA", "HASAN", "KAMİL", "NEBİ", "ÖZCAN",
			"NAGİHAN", "CEREN", "SERKAN", "HASAN", "YUSUF KENAN", "ÇETİN", "TARKAN", "MERAL LEMAN", "ERGÜN",
			"KENAN AHMET", "URAL", "YAHYA", "BENGÜ", "FATİH NAZMİ", "DİLEK", "MEHMET", "TUFAN AKIN", "MEHMET",
			"TURGAY YILMAZ", "GÜLDEHEN", "GÖKMEN", "BÜLENT", "EROL", "BAHRİ", "ÖZEN ÖZLEM", "SELMA", "TUĞSEM",
			"TESLİME NAZLI", "GÜLÇİN", "İSMAİL", "MURAT", "EBRU", "TÜMAY", "AHMET", "EBRU", "HÜSEYİN YAVUZ", "BAŞAK",
			"AYŞEGÜL", "EVRİM", "YASER", "ÜLKÜ", "ÖZHAN", "UFUK", "AKSEL", "FULYA", "BURCU", "TAYLAN", "YILMAZ",
			"ZEYNEP", "BAYRAM", "GÜLAY", "RABİA", "SEVDA", "SERHAT", "ENGİN", "ASLI", "TUBA", "BARIŞ", "SEVGİ",
			"KALENDER", "HALİL", "BİLGE", "FERDA", "EZGİ", "AYSUN", "SEDA", "ÖZLEM", "ÖZDEN", "KORAY", "SENEM",
			"ZEYNEP", "EMEL", "BATURAY KANSU", "NURAY", "AYDOĞAN", "ÖZLEM", "DENİZ", "İLKNUR", "TEVFİK ÖZGÜN",
			"HASAN SERKAN", "KÜRŞAT", "SEYFİ", "ŞEYMA", "ÖZLEM", "ERSAGUN", "DİLBER", "MESUT", "ELİF", "MUHAMMET FATİH",
			"ÖZGÜR SİNAN", "MEHMET ÖZGÜR", "MAHPERİ", "ONUR", "İBRAHİM", "FATİH", "SEVİL", "SÜHEYLA", "VOLKAN", "İLKAY",
			"İLKNUR", "ZÜMRÜT ELA", "HALE", "YENER", "SEDEF", "FADIL", "SERPİL", "ZÜLFİYE", "SULTAN", "MUAMMER HAYRİ",
			"DERVİŞ", "YAŞAR GÖKHAN", "TUBA HANIM", "MEHRİ", "MUSTAFA FERHAT", "SERDAR", "MUSTAFA ERSAGUN", "ONAT",
			"ŞÜKRÜ", "OLCAY BAŞAK", "SERDAR", "YILDIZ", "AYDIN", "ALİ HALUK", "NİHAT BERKAY", "İSMAİL", "AYKAN",
			"SELÇUK", "MEHMET", "NEZİH", "MUSTAFA", "TİMUR", "ERHAN", "MUSTAFA", "MUTLU", "MEHMET HÜSEYİN",
			"İSMAİL EVREN", "OSMAN ERSEGUN", "MEHMET", "ELİF", "SERKAN", "MESUT", "MEHMET HİLMİ", "ASUDAN TUĞÇE",
			"AHMET GÖKHAN", "BAŞAK", "CEYHAN", "MUHAMMET TAYYİP", "ESİN", "ZEYNEP GÖKÇE", "EVRİM", "YASİN", "SALİHA",
			"DENİZ", "BELGİN", "ÖZLEM", "GONCA", "ESRA", "SEÇKİN", "ESRA", "FATİH", "MUSTAFA", "FEVZİYE",
			"MUSTAFA ARİF", "BİRGÜL", "ÖZLEM", "ÖZLEM", "FUNDA", "BERFİN", "DEMET", "SONAY", "SERÇİN", "ALMALA PINAR",
			"ÜMİT", "SENEM", "DENİZ", "MÜNEVER", "HATİCE", "ÖZLEM", "ÖZLEM", "ALİ SEÇKİN", "COŞKUN", "ÖZGE", "ZELİHA",
			"PINAR", "AYBÜKE", "HASİBE", "GÜRKAN", "ZÜHAL", "NAZIM", "ZEYNEP", "OSMAN", "AYLA", "BEYZA", "ELİF", "ERAY",
			"DİANA", "TUBA", "SEMRA", "VELAT", "BELGİN EMİNE", "SİBEL", "GÖKMEN ALPASLAN", "BENHUR ŞİRVAN", "DİLEK",
			"HANDE", "ŞAHABETTİN", "MİRAY", "ZERRİN", "İLKNUR", "ELİF", "MÜMTAZ", "TUĞBA", "DİLEK", "MEHMET BURHAN",
			"FUAT", "NİHAL", "AYŞEGÜL", "SEMA", "ZAFER", "NURSEL", "GÜLPERİ", "BİLGE", "FATİH", "CENGİZ", "SİMGE",
			"SEMA NİLAY", "EMİNE", "RİFAT CAN", "SİNAN", "LATİFE", "MEHMET", "NURDAN", "MELTEM", "ÜLKÜHAN", "HASAN",
			"GÜLDEN", "SAMET", "BERNA", "ÖZLEM", "NAFİYE", "KENAN", "SERKAN FAZLI", "NURSEL", "ABDULLAH", "ERGÜL",
			"HASAN", "MUSTAFA", "SEBAHAT", "EMİNE", "ERDAL", "LEZİZ", "BİRSEN", "TUBA", "AYŞEN", "EBRU", "TAYFUR",
			"MELTEM", "SERHAT", "AYCAN ÖZDEN", "ELİF", "SEVGÜL", "SELDA", "IŞIL", "SİBEL", "JÜLİDE ZEHRA",
			"BERİL GÜLÜŞ", "İNCİ", "ENGİN", "GÜLBAHAR", "MÜBECCEL", "NURDAN", "HANDE", "ÖZNUR", "HANDAN",
			"OSMAN TURGUT", "EMİN TONYUKUK", "NEJDET", "MUSTAFA", "GÜLİZ", "İPEK", "NİHAL", "MELDA", "DERYA", "DEMET",
			"MAHMUT", "EMEL", "ÖZNUR", "SONGÜL", "RESA", "GAMZE", "ÜMİT", "DENİZ", "MUAMMER MÜSLİM", "ÖMER FARUK",
			"TUĞÇE", "VELİ ENES", "ZAHİDE", "NURETTİN İREM", "SEDAT", "REMZİYE", "SİBEL", "İLKNUR", "YASEMİN", "AYLİN",
			"EMEL", "EMEL CENNET", "ŞAFAK", "METİN", "SÜLEYMAN", "MUKADDES", "BARIŞ", "MEHMET ALİ", "TEVFİK", "SERDAR",
			"EMİNE", "MÜRŞİT", "MUTLU", "FEZA", "İBRAHİM TAYFUN", "SERKAN", "AHMET SERKAN", "FATMA", "BERKER", "SERDAR",
			"KUBİLAY", "ERKAN", "KERİM", "İLKNUR", "SERKAN", "MUSTAFA", "RUKİYE", "GÖKTEN", "SEZGİ", "TUĞBA", "MURAT",
			"HATİCE", "HATİCE EYLÜL", "AYŞE GÜL", "NEVİN", "HABİBE", "KEZBAN", "AYSEL", "TALHA", "DUYGU", "GÖZDE",
			"FIRAT", "EBRU", "GÜLEN ECE", "SİBEL", "FULYA", "VEDAT", "HARUN", "FİLİZ", "NURAY", "ŞİRİN", "ÖZLEM",
			"BURCU", "PINAR", "HATUN", "CEYDA", "BURCU", "AYŞE", "ALPER", "FEYZA", "HACI MURAT", "MÜCELLA", "FEYZAHAN",
			"ŞENAY", "MERİH", "YUSUF", "ARDA", "EVRE", "KONURALP", "KIVANÇ", "EMİNE", "VOLKAN", "NİHAT",
			"RENGİN ASLIHAN", "EMRE", "ARİFE ESRA", "SEDAT", "MURAT", "CEM", "ERHAN", "ÖMÜR", "UMUT CAN",
			"MUSTAFA NAFİZ", "DAMLA", "MÜSLİM", "ABDULKADİR", "SAADET", "REZZAN", "SEDAT", "İBRAHİM", "LEYLA", "TÜLAY",
			"ENDER", "YELİZ", "ÖZGÜL", "HALE", "BERÇEM", "MUSTAFA", "TUBA", "SABAHATTİN", "ŞAFAK", "EVRİM", "REŞAT",
			"MUMUN", "FUNDA ÖZLEM", "NUR", "METE", "TÜLAY", "ÖZLEM", "HATİCE NİLDEN", "MELTEM", "EDA", "MELTEM",
			"MUSTAFA", "YURDUN", "SEMA", "TUBA", "SERPİL", "CENK", "TANER", "ZEKERİYA", "MUHAMMED ALİ", "TUĞRUL",
			"YÜCEL", "ESMA ÖZLEM", "AHMET", "SEVDE NUR", "SAMİ", "GAMZE", "GÜLSÜM", "SERHAT", "BARIŞ", "GÜLSEREN",
			"SULTAN", "İLKER", "DERAM", "AHMET", "BALA BAŞAK", "FEVZİ FIRAT", "GÖZDE", "FERHAN", "İLKER", "SALİM",
			"EMİNE", "MURAT", "ATAKAN", "REFİK", "MUSTAFA", "ÖZGÜR", "İKLİL", "ZÜHAL GÜLSÜM", "MEHTAP", "DENİZ", "ÜMİT",
			"MEHMET", "VOLKAN", "İLKNUR", "SELÇUK", "ÖZLEM", "GÖKHAN", "METE", "HÜMEYRA", "PAPATYA", "LEVENT",
			"FADİME SEVGİ", "ERSEN", "ŞULE MİNE", "MELİA", "ŞERMİN", "AYLİA", "AHMET EMRE", "VEDAT", "HALUK", "SEZGİN",
			"ZEHRA BETÜL", "VOLKAN", "ÜNSAL", "KORAY", "GÜLŞAH", "HİCRAN", "YUSUF KENAN", "YUSUF", "ORHAN", "FÜSUN",
			"ÖZLEM", "MEHMET", "SERKAN", "İKRAM", "ÜLKÜHAN", "NUH", "İSMAİL", "GÜLŞAH", "AYKUT", "NEŞE", "NEZAKET",
			"MUHAMMET DEVRAN", "HANDAN", "ATİLLA", "AYŞEGÜL", "PINAR", "ARZU", "NEŞE", "CEYHAN", "HASAN SAMİ", "MEHMET",
			"SALİH", "FERDA", "DİLEK", "AYHAN", "HASAN ULAŞ", "SUNA", "SELAMİ", "SÜREYYA", "BURCU", "CEM YAŞAR",
			"ÇAĞDAŞ", "DOĞAN", "AHMET", "HATİCE", "HAYRİ", "GÜHER", "SUNA", "ARZU", "AHMET", "AHMET", "SEMİH", "YUSUF",
			"ALİ", "ELİF ÇİLER", "ELİF", "YETKİN", "BURAK", "VEYSEL", "BURCU", "REFAETTİN", "AYŞE GÜL",
			"HÜSEYİN KUNTER", "EMİNE", "EBRU", "ESRA NUR", "SAMİ", "MUSTAFA GÜRHAN", "ORHAN", "HAÇÇE", "MEHMET MURAT",
			"NAZAN", "TUĞBA", "OĞUZHAN", "BERFİN CAN", "ÖZGÜR", "CİHAN", "DERMAN", "NİHAL", "IŞIN", "HATİCE", "DİDEM",
			"SUAT", "SİMENDER", "ADNAN", "SEZİN", "ŞERAFETTİN", "BERRİN", "ÖZGÜR", "TAYFUR", "SERHAT", "FUNDA",
			"NESLİHAN", "SERVET", "EMRE", "ORÇUN", "FATMA ESİN", "DERYA", "DUYGU", "HÜSEYİN", "AYKUT", "AYŞE",
			"SEHER ÖZLEM", "SEDA", "ESRA CAN", "EVREN", "NİLÜFER", "MURAT", "YUSUF", "MUSTAFA BARAN", "GÜNEŞ", "FATİH",
			"MEHMET", "ORHAN", "PINAR", "ERHAN", "GÜLDEN", "LATİFE", "SİBEL", "FATMA SELCEN", "HALİS", "YELDA", "ZEHRA",
			"MEHMET REŞİT", "EMCED", "OKAN", "ABDULLAH ARİF", "ÖZLEM", "AYDEMİR", "FATİH", "AYDIN", "BENGÜHAN", "AHMET",
			"TOLUNAY", "TUĞRA", "ÖZGÜR", "YUSUF", "ÖNDER TURGUT", "BARAN", "SEYHAN", "ZEKİ", "KADİR", "MURAT", "İHSAN",
			"DEMİR", "MUHAMMET MURAT", "MUHAMMED", "BAHADIR", "İLHAN", "YILDIRIM", "OĞUZ KAAN", "EFTAL MURAT", "GÖKAY",
			"FATİH RIFAT", "NURAN", "HABİL", "ÖMER ÖZKAN", "SELMA", "NURETTİN", "AHMET", "UTKU", "SÜLEYMAN", "CAN",
			"ONUR KADİR", "FİLİZ", "CANSU SELCAN", "ABDULLAH", "NESLİHAN", "SEDA ELÇİM", "KÜBRA", "HÜSEYİN", "BELMA",
			"MÜCAHİT", "CEYHUN", "GÜLTEKİN  GÜNHAN", "HANDE", "GÜLHANIM", "ÖMER", "ZİYA", "ÇAĞRI", "SERKAN", "LEVENT",
			"İSA", "CEM", "HÜSEYİN", "CEMİLE ÇİĞDEM", "MAHMUT", "ÖNDER", "RAŞAN", "İSMAİL YAVUZ", "ÖMER",
			"KENAN SELÇUK", "EMRE", "SERDAR", "SONER", "ENVER", "MUHLİS", "TİMUR", "LEMAN", "ELA", "CEMİL", "BURCU",
			"SALİHA SANEM", "ZELİHA", "ALEVTİNA", "ARZU", "MEHMET", "BİREYLÜL", "AYSEL", "ÖZGÜL", "SIDIKA", "TUNA",
			"ÖVGÜ ANIL", "EMİŞ", "NİLGÜN", "ELİF", "SEBİHA", "YUSUF ALPER", "IŞIL", "AYŞEGÜL", "TÜMAY", "ZERİN",
			"MEHMET", "FATMA ECE", "AHMET", "NEVROZ", "SEMA", "GAMZE PINAR", "FATMA", "MELTEM HALE", "SELMA", "İBRAHİM",
			"ASLIHAN", "FİLİZ", "BİLGİ", "TUĞBA", "HÜSEYİN", "EVREN", "FERDİ", "BURÇİN", "BARIŞ", "BAVER", "MAHMUT",
			"EMRAH KEMAL", "MURAT", "MEHMET ÖZER", "GÖKAY", "NECİP", "ERKAN", "ÜMİT", "GANİM", "BARAN", "FATİH",
			"SANCAR", "MUSTAFA KEMAL", "DURAN", "HASAN", "GÖRKEM", "ALİ OZAN", "VELİ ÇAĞLAR", "İRFAN", "SEYFİ CEM",
			"CÜNEYT", "ALİ", "FIRAT", "MUHAMMED TAHA", "BURAK", "MUSTAFA", "NİZAMETTİN", "RECEP GANİ", "ARZU",
			"MAHMUT NURİ", "GÜNAY", "BESTE", "CEM", "EMRAH", "HALİL İBRAHİM", "ESEN İBRAHİM", "MELİKE", "MÜRSEL",
			"KORAY", "UMUT SİNAN", "İBRAHİM BARIŞ", "BURHAN", "MUSTAFA KÜRŞAT", "SERDAR BORA", "FUAT", "ELİF", "SİBEL",
			"ADEM", "CEM İNAN", "GÖKTEKİN", "DİLARA", "SEDA", "CUMHUR", "HALUK", "PINAR", "AYKUT", "ÖYKÜ", "BAŞAK",
			"ÖMER", "SERHAN", "SULTAN", "MİNE CANSU", "SUAT", "ÇİĞDEM", "HANDE", "UMUT SEDA", "EVRİM", "DİCLE", "İREM",
			"FATMA", "İLKNUR", "CEMİLE AYŞE", "FEYZA", "MUAMMER", "EBRU", "DİNÇER AYDIN", "AYŞE AHSEN", "PINAR",
			"SÜREYYA BURCU", "MARİA", "SEÇİL", "BARIŞ", "ŞAHİNDE", "MEHMET", "NEVRİYE", "NİLAY", "RÜŞTÜ", "ASLI",
			"TANSU", "ASLIHAN", "NURCAN", "DEMET", "ERDEM", "BETÜL EMİNE", "ÇETİN", "PINAR", "RASİM", "AHMET", "ZEHRA",
			"SELİM", "DENİZ", "MESUT", "AYSEL", "MÜMÜNE", "DUÇEM", "YAKUP", "ERCAN", "MEHMET GÖKÇE", "ATİLLA SÜLEYMAN",
			"MERAL", "NURDAN", "SEÇİL", "ELİF", "HASAN BİLEN", "NİLAY", "HİLAL", "MUSTAFA", "ŞEYMA MELİHA", "MÜJDAT",
			"BURCU", "ARİF", "AYŞE", "HACİ HALİL", "IŞIK", "SERAY", "SERHAT BURKAY", "EMİN", "ÇAVLAN", "ŞEREF CAN",
			"KADİR", "MÜBERRA", "AYŞE", "HASAN", "SAVAŞ", "AYŞE NUR", "SÜLEYMAN", "DUYGU", "HACI MEHMET", "MEHTAP",
			"ERKAN", "SEMİNE", "ELİF", "RAMAZAN", "AHMET EMRE", "SERKAN", "DENİZ", "MİNE", "FATİH", "İREM", "ENDER",
			"YASEMİN", "SEMA", "MUSTAFA ULAŞ", "ALİ", "EMİNE", "TÜLAY", "BİRSEN", "GÜLSEN", "TUBA", "HAYRİYE",
			"BAHADIR", "SEZEN", "EDİP GÜVENÇ", "MEHTAP", "FİLİZ", "EYLEM", "RAMAZAN", "BİLGİN", "ESRA", "ATİYE MELTEM",
			"METİN", "ÖZLEM", "OSMAN", "AYŞE", "HİLAL", "YAVUZ", "ÖZLEM", "AYŞE", "MUSTAFA", "ASUMAN", "ÖMER", "ŞENAY",
			"GÜLNAME", "ÖZLEM", "BETÜL", "EMİNE DİLEK", "ZELİHA", "ESİN SEREN", "FİLİZ", "ULAŞ", "HALE", "ADEM",
			"İLKER", "ERHAN", "FARUK", "İBRAHİM", "SERKAN", "MAHMUT ESAT", "ERAY", "OKTAY", "DENİZ", "OSMAN", "İZZET",
			"İHSAN", "URAL", "ARİF", "AZİZ", "FUAT ERNİS", "HAKAN", "MUZAFFER OĞUZ", "MAHİR", "TAYYAR ALP", "EREM",
			"CİHAN", "MEHMET", "ÜMİT", "MEHMET", "YASEMİN", "CEYDA", "FATIMA İLAY", "NİLAY", "HURİYE", "MERVE", "ŞEYMA",
			"SELAHATTİN", "İLKER", "ÖMER", "SÜLEYMAN", "TAYFUN", "ESER", "MEHMET CİHAT", "İSMAİL", "ABDULLAH", "MURAT",
			"MUSTAFA", "AYDOĞAN", "HAMDİYE", "KERİME RUMEYSA", "MELAHAT", "UYSAN", "EMRAH", "HAYRİ ÜSTÜN", "MURAT",
			"HÜSEYİN SAİD", "SELİM", "ZEKERİYA", "FATİH", "DERYA", "NEVAL", "SEZER", "ÖZGE", "HATİKE", "ÖZGÜR", "ŞAFAK",
			"AHMET TUNÇ", "ÖZDEN", "OKAN", "RAZİYE DAMLA", "MAHMUT", "LALE", "EYÜP", "DERYA", "SELAHATTİN", "ZEKİ",
			"HÜSEYİN", "MELEK", "BİNNUR", "ONUR SALİH", "ERKİN", "MURAT", "ERHAN", "BURAK", "AYŞE CEREN", "NUMAN",
			"ÖZLEM", "ÜZEYİR", "CAN", "MUSTAFA", "KORHAN", "ALPER", "FUNDA", "HAMZA", "MUSTAFA RAŞİD", "YUSUF",
			"MELİKE", "ŞÜKRÜ", "KASIM", "GÖKSEL", "HİKMET", "ASIM", "ÜMRAN", "TEKİN", "MURAT", "HACER", "BERAY",
			"HANIM", "EDA", "NURAY", "ÖMER FARUK", "FERHAT", "OĞUZ", "BENGÜ", "AHMET MERT", "PINAR", "EMRE", "KÜBRA",
			"SELÇUK", "ŞENOL", "ORHAN", "MÜMÜN FATİH", "MURAT", "MERT", "KADİR", "KAAN SUAT", "ERCAN", "GÖKHAN",
			"DENİZ", "CİHAN", "CEM", "ALİŞAN", "ALİ SAİP", "ABİDE MERVE", "YUSUF", "ÖMER", "SERDAR", "YÜKSEL", "AHMET",
			"MAHMUT", "BURAK", "YASEMİN", "İBRAHİM FUAT", "MUSTAFA", "BİLGE MERVE", "AHMET CEVDET", "SEHER",
			"MEHMET DENİZ", "SAMET", "SAMET SANCAR", "ŞULE", "SELİM", "KADİR", "CAHİT", "HAYRUNNİSA", "UĞUR", "RESUL",
			"MUSTAFA BUĞRA", "İSHAK", "ESRA", "SANEM GÖKÇEN MERVE", "EZGİ", "MUSTAFA GÜVENÇ", "OĞUZ KAĞAN", "ESMA",
			"ÖMER AYKUT", "MEHMET ŞİRİN", "OSMAN", "AHMET", "NAİL", "İREM", "ELA", "BAHRİ", "ALİ RIZA", "ADEM",
			"MEHMET", "BAYRAM FURKAN", "HALİL", "KÜBRA", "SEFA", "MEHTAP", "FATİH", "BERKAN", "HÜSEYİN", "VOLKAN",
			"ŞEBNEM", "TUBA", "AYHAN", "ENNUR", "ALİ RIZA", "FATİH", "GÖZDE", "SEVİL", "EDA", "HATİCE", "ZARİFE",
			"BURAK", "İREM", "KÜBRA", "EMİNE", "DİLEK", "TUBA", "HALİL", "ESER", "EBUBEKİR ONUR", "MEHMET VEYSEL",
			"BURCU", "AYŞEGÜL", "DERYA SEMA", "ÇAĞLA", "KADİR", "BURAK", "MELİKE", "BAYRAM", "AYŞEGÜL", "TUĞBA",
			"MEHMET", "ELİF", "TÜRKER", "SEMİH", "HATİCE", "CANSU", "SERHAT", "TANER", "İDRİS", "SEVİNÇ", "FERİDE",
			"EVREN", "TUĞBA", "MUHAMMED MUCAHİD", "HİLAL", "DİLEK", "NAZLI", "ŞİFA", "GÜLÇİN", "KEZBAN", "YELDA",
			"SEDA", "CEYLAN", "BURAK", "UĞUR", "ŞEYMA", "SELMA", "EMRE", "MUHAMMED FATİH", "AHMET", "HASAN", "RUKİYE",
			"ADİL", "MUSTAFA", "MUSTAFA ASIM", "METİN", "CEREN", "SÜLEYMAN", "MELİKE", "TUĞBA", "ÇAĞRI", "BURHANETTİN",
			"İBRAHİM", "YUNUS EMRE", "KADİR", "MEHMET", "İLKER", "GÖKHAN", "KEMAL KÜRŞAT", "AHMET", "ZEHRA", "AZİZ",
			"İSA", "GÖKHAN", "BARIŞ", "MEHMET SUPHİ", "ASİYE BURCU", "ZEYNEP", "YÜKSEL UĞUR", "VOLKAN", "UFUK", "TUĞBA",
			"SELİM", "SELÇUK", "SALİHA DİLEK", "ÖZKAN", "ÖVÜNÇ", "OSMAN", "NUR ALEYNA", "MUHAMMED FURKAN",
			"MEHMET VEHBİ", "MEHMET İKBAL", "MEHMET", "LEYLA", "İZZETTİN", "İSMAİL", "İSMAİL", "İHSAN BURAK", "İBRAHİM",
			"HİLAL", "HAYRİYE", "GİZEM", "ESİN", "ERCAN", "ENES TAHİR", "DİDEM", "DENİZ", "BUĞRA", "BİLGİN",
			"ALİ MUHARREM", "ALİ", "ADEM", "ADEM", "IŞIL", "YUSUF", "MUSTAFA", "HÜSEYİN", "EZEL", "EMİNE", "ELİF CEREN",
			"AYŞE GİZEM", "DEMET", "DİDEM AYŞE", "ESER", "AYŞE GÜLÇİN", "HACER", "FERAT", "ZEYNEP", "KIYMET", "ASLAN",
			"ALİ", "GİZEM", "CUMA", "MEHTAP", "HAMDİ", "ORHAN", "NURİ ANIL", "SEMRA", "ÖKKEŞ", "VEYSEL",
			"ŞERİFE YASEMİN", "MEHMET", "MEHMET", "TOLGA", "OĞUZHAN", "MUSTAFA", "MAHSUM", "İSMAİL", "MEHMET", "PERVİN",
			"MURAT", "SALİM", "YUNUS", "VEYSEL", "ÖMER", "AHMET", "AYDIN", "ABDULKADİR", "AHMET", "AYŞEGÜL", "AZİZ",
			"ABDULLAH", "ABDULLAH", "YILDIRIM", "AYŞEGÜL", "İBRAHİM", "SİBEL", "MUZAFFER", "MUHAMMED YUSUF", "HAMZA",
			"HAKAN", "NESRİN", "EMRAH", "KEZİBAN", "MEHMET ALİ", "MELİKE ELİF", "BURHAN", "CEVAHİR", "CİHAN", "EMRAH",
			"MEHMET ALİ", "FATİH", "MAHMUT", "FATİH", "DAVUT", "EDA", "ÖMER FARUK", "PELİN", "SALİH", "YUSUF ZİYA",
			"ZERİN", "ÖZKAN", "YUSUF", "UMUT", "EMRAH", "ŞEYHMUS", "MERT METİN", "MUKADDES", "HAZAN", "HALİM",
			"MEHMET AKİF", "AYCAN", "CEMİL", "BERNA", "AHMET", "HÜSEYİN", "ERKAN", "HAKAN", "BAYRAM", "RUMEYSA", "ÇİLE",
			"VEYSİ", "ZUHAL", "YÜCEL", "ESMERALDA", "NİLÜFER", "BURÇHAN", "VOLKAN", "SELCEN", "MUSTAFA", "HATİCE",
			"BURCU", "AHMET BURAK", "BÜLENT", "MÜCADİYE", "SONGÜL", "IŞIK MELİKE", "MEHMET RAŞİT", "AYHAN", "SERDAR",
			"ERKAN", "EMRAH", "ERDAL", "RAHİME", "GÜLDEN SİNEM", "İHSAN", "ABDUL AZİM", "ÖZTAN", "ŞENOL",
			"MEHMET HAZBİN", "İSMAİL ŞENOL", "BORA", "HAMZA", "SEDA", "FATİH", "UĞUR", "KEMAL", "ADİL", "MAZLUM",
			"CEM ATAKAN", "VOLKAN ", "MUHAMMED ", "ERDEM", "FATMA", "HATİCE", "MEHMET EVREN", "ATACAN", "FATMA", "ŞADİ",
			"GÜLCAN", "MERYEM", "KAMERCAN", "ESRA", "ŞERİFE SEÇİL", "ZEHRA", "VAZİR AKBER", "ZEYNEP", "ALİ SAİD",
			"MURAT", "UTKU", "SAİD", "İRFAN", "HALİME", "ÜMMÜ GÜLSÜM", "ŞAFAK", "BETÜL", "BAŞAK", "ZEYNEP EZGİ",
			"VASFİYE", "ERDEM", "İBRAHİM", "AYŞE", "MEHMET VEHBİ", "FATMA", "EMEL", "ADEM", "ESEN", "HACI KEMAL",
			"EMRE", "ÖZGÜR", "OSMAN", "ÇETİN", "SONGÜL", "MUSTAFA", "ÖZLEM", "ZAFER", "FATİH", "MAKBULE SEDA",
			"FATMA BEGÜM", "TUĞBA", "OSMAN", "SİNEM", "ANIL", "KEMAL", "EREN", "BİLAL BARIŞ", "İBRAHİM", "ATILGAN",
			"BERNA", "SERAP", "BEKİR", "EYÜP", "EGEMEN", "ALİ", "YÜCEL", "TANER", "VAHDETTİN TALHA", "TOLGAHAN",
			"MUKADDER", "ENGİN", "SERTAÇ", "CİHAN", "PINAR", "AYŞE", "UĞURAY", "MUSTAFA", "EDA", "ELİF", "TAYLAN UĞUR",
			"ABİDİN", "BULUT", "CUNDULLAH", "ADNAN", "ERSİN", "ERHAN HÜSEYİN", "OĞUZ", "HAYRİ", "NURİ", "SELMA", "ESİN",
			"YAKUP İLKER", "AHMET", "ABDULLATİF", "FATİH", "ÖZLEM", "İNANÇ", "HALİL İBRAHİM", "ÖZGE", "HASAN", "PINAR",
			"AYŞE", "AYŞE", "HALİL CAN", "CEM ÖZGÜR", "SEDA", "UMUT", "VOLKAN ONUR", "EMRAH", "NAGİHAN", "HÜSEYİN",
			"MEHMET DİRİM", "MİNE", "GİZEM", "ÖMER", "TUĞBA", "MERVE", "YÜCE", "AYŞE PINAR", "AHMET CAN", "MERVE",
			"EMRE", "ERDİNÇ", "NİLAY", "BUŞRA", "ZÜHAL", "CAFER", "İLKER", "TURAN", "RECEP", "EBRU", "ÖMER FARUK",
			"MEHMET NURİ", "HİLAL", "NURHAN", "YASEMİN", "NAGİHAN", "ALİ KEMAL", "PINAR", "GİZEM", "UYGAR", "SEFA",
			"NURULLAH", "GİZEM", "SEVAL", "ŞERİF", "BERFU", "SEMİH", "SIDDIKA", "TUĞBA", "İRFAN YILDIRIM", "FAHRİ",
			"CANSU", "PETEK", "ELİF", "CEM", "EMRAH", "BÜŞRA", "NAFİZ", "ŞEYMA", "EKREM", "YASEMİN", "AYŞE", "ÇAĞRI",
			"MEHMET", "ÖMER GÖKHAN", "REYHAN", "HÜSEYİN", "ÖZGE", "DERYA", "MUSTAFA", "ESMA", "ÖZLEM", "GÖZDE", "SELİM",
			"EMİNE", "ÇİĞDEM", "ŞERİFE EZGİ", "NEFİSE", "TAYFUN", "PELİN", "ÖZGE", "KADER", "BELGİN", "ABDULAZİZ",
			"EMEL", "NUR", "BURCU", "CEYDA", "VEHBİ", "RAHİME", "ALİ", "DENİZ", "MUHAMMED", "EMRE", "HASAN",
			"ÜBEYDULLAH", "EYÜP GÖKHAN", "KÜBRA", "İTİBAR", "MUSTAFA ABDULLAH", "TUBA", "NURDAN", "HATİCE", "BÜŞRA",
			"ZEHRA", "CANAN", "OSMAN BİLGE", "TUBA", "FARİS", "AYHAN", "TURĞUT", "ONUR", "MERVE", "HİDAYET", "TUBA",
			"ABDULKERİM", "MUHAMMED RIZA", "DİLEK", "ARDA NERMİN", "FAİK", "HALİL", "HASAN HÜSEYİN", "SEMA", "MUSA",
			"AYŞEGÜL", "BÜŞRA", "ZEYNEP", "METİN", "CİHAN", "MERAL", "HİLAL", "DİDEM", "İSMAİL", "HASAN CIVAN",
			"MEHMET AKİF", "FATİH", "CEM", "YUSUF", "HÜSEYİN", "TUBA", "TAYFUN", "MUSTAFA", "HASAN", "RUKİYE ÖZDEN",
			"MEHMET", "ESRA", "HASAN", "MUSTAFA CİHAD", "EYÜP", "MURAT", "İBRAHİM HALİL", "SEDAT", "MEHMET", "HÜSEYİN",
			"MURAT", "HACI", "AYŞE FULYA", "FAİK", "RAMAZAN", "MEHMET", "SAVAŞ", "AYHAN", "NAİME SILA", "RIFAT",
			"MURAT", "FİKRİ", "HASAN", "YURDAGÜL", "MUHAMMET MUSTAFA", "ERCAN", "TUĞBA", "MEHTAP", "HAMİT", "FERİT",
			"DENİZ", "MUSTAFA", "ŞAHİKA", "SERDAR", "TAHSİN BATUHAN", "ONUR", "MURAT", "EMİNE AYÇA", "İLYAS", "ECE",
			"MUSTAFA", "MURAT", "AHMET ÇAĞRI", "NİLGÜN", "MUSTAFA", "ERHAN", "VEYSEL", "HAKAN", "FATİH", "BARIŞ",
			"ANDAÇ", "ŞENOL", "HASAN", "KEMAL", "CEYHUN", "ABDÜLSAMET", "İBRAHİM", "AHMET", "SERPİL", "ÜMMÜGÜLSÜM",
			"YAVUZ", "SÜHEYLA AYÇA", "ÖZGE", "TUBA", "MERT", "ÖZGÜR", "MELİHA ESRA", "BETÜL", "ABDULLAH", "SİNAN",
			"GÜNDEM", "KEZİBAN", "DERYA", "OĞUZ", "BEDRİ", "MEHMET", "EMRE", "ÜMİT", "HALİL İBRAHİM", "ÖZGE", "HAZEL",
			"FATMA BETÜL", "MUSTAFA", "AHMET BİLGEHAN", "HİKMET EKİN", "EMİN", "YİĞİT", "ABDULSAMET", "PINAR",
			"MUSACİDE ZEHRA", "BURAK", "TUĞÇE", "MAHMUT BAKIR", "ELİF TUĞÇE", "EKİN", "ASLIHAN ESRA", "İSMAİL MİKDAT",
			"UFUK", "MEHMET NURİ", "BİLGE", "ELİF AYŞE", "ERKAN SABRİ", "İREM", "BÜŞRA SULTAN", "MUSTAFA ALİCAN",
			"AYBEGÜM", "NEFİSE", "ZEKERİYA ERSİN", "AHMETCAN", "ABDURRAHMAN FUAT", "ZEYNEP", "FATİH", "ÇAĞDAŞ",
			"UĞURAY", "SARE", "ONUR", "SERAP", "MURAT", "EMİNE", "MUSTAFA", "BAHADIR", "ZEYNEP", "GÖZDE KÜBRA", "BURAK",
			"MEHMET", "MUHAMMED HASAN", "İSMAİL", "SAYGIN", "GÖKHAN", "ALPER", "ÇAĞLAR", "CEMAL", "HÜSEYİN ONUR",
			"CEMRE", "MELTEM", "CEYHUN", "ASLI", "İBRAHİM", "GÖZDEM", "HİLAYDA", "HASRET", "ŞEBNEM", "ONUR", "CAN",
			"AYŞEGÜL", "EMİNE", "PELİN", "ARMAN", "MERVE", "GAMZE", "ALİŞAN", "ERDEM", "ENES", "ORGÜL DERYA", "BAŞAK",
			"HASAN KADİR", "ŞADİYE SELİN", "CEREN", "ELİFCAN", "SEVAL", "GÖKÇEN", "ÖMER FARUK", "ALİ HASAN", "MUSTAFA",
			"KÜBRA", "HACI HASAN", "GÖKHAN", "MERİÇ", "İREM", "MAHMUT SAMİ", "MURAT", "SÜLEYMAN SERDAR", "NİHAN",
			"HANİFE TUĞÇE", "ŞULE", "ERTAN", "MUHAMMED", "ÇAĞDAŞ", "ALİ", "YUSUF", "HATUN", "İBRAHİM", "MEHMET YAVUZ",
			"MEHMET", "ERTAN", "SÜMEYRA", "FATMA EFSUN", "RAMAZAN", "MEHMET ALİ", "BERKAN", "DAVUT", "YAVUZ SELİM",
			"İBRAHİM", "OKAN", "MEHMET", "MERT", "EMRAH", "BURAK", "ALAADDİN", "İDRİS BUGRA", "KÜBRA", "AYŞEGÜL",
			"MERVE NUR", "SUNA", "ELZEM", "ABDURRAHMAN", "BURAK", "MİHRİMAH SELCEN", "RABİA", "MERİÇ", "DERYA",
			"SERKAN", "CEYDA", "AYŞENUR", "MEHMET AKİF", "TUĞBA", "SERHAT", "BAYRAM", "MEHMED UĞUR", "DAMLA", "MUSTAFA",
			"VEYSEL", "AHMET SERKAN", "GÖKHAN", "ABDULSELAM", "MEHMET YILDIRIM", "HÜSEYİN", "BİLAL", "YAKUP",
			"GÜLLÜ SELCEN", "VEHBİ", "DİLAN", "ÖMER FARUK", "EMRE", "MEVLÜT", "NEBİL", "SIDIKA", "FATMA", "MARUF",
			"FIRAT", "EBRU", "MAHMUT", "PINAR", "ALİ", "OSMAN", "HİLAL", "NİLGÜN", "BARIŞ", "ERKAN", "CİHAN", "ÖMER",
			"BEGÜM", "SÜLEYMAN", "ÖMER", "ELİF", "MEHMET ZİYA", "HAKAN", "AHMET SERCAN", "BİŞAR", "MUSTAFA", "NUR",
			"SERKAN", "ELİF", "HALİL İBRAHİM", "EMİNE", "FATİH MEHMET", "EREN", "AYTAÇ", "ENGİN", "YURDAGÜL", "FULYA",
			"YASEMİN", "ETHEM", "YASİN", "METİN", "ELİF", "FERAT", "YUSUF", "CİHAN", "MUHAMMED", "GÜLŞEN", "ABDULMELİK",
			"MERVE", "ALİ", "YASEMİN", "MUHAMMET BAĞBUR", "YUSUF ZİYA", "ERDAL", "MURAT", "MERVE", "RAMAZAN", "GÜL",
			"BEGÜM", "ORHAN UYGAR", "HASAN", "SEMA", "GÜNEŞ", "BEYZA", "FIRAT", "YİĞİT", "MUHSİN", "HAMZA",
			"SABAHATTİN", "EMİN", "AHMET ÇAĞRI", "NAZLI", "MEHMET", "GÖKSEL", "BAŞAK", "HASAN YAVUZHAN", "GÖKHAN",
			"MUHAMMED SAMİ", "ENGİN", "EBRU", "METANET MEHRALİ", "EZGİ GİZEM", "NESLİHAN", "İLYAS", "SERKAN",
			"MEHMET SELAHATTİN", "MUSTAFA", "MEHMET", "CİHAN", "MURAT", "KASIM", "RAHİME MERVE", "OZAN", "ÖZKAN",
			"ZAFER", "SENAN", "BEDREDDİN", "MURAT", "ZEYNALABİDİN", "CAHİT", "DENİZ", "MEHMET EMRAH", "ESRA", "TUNCAY",
			"HÜSEYİN", "YİĞİT", "BÜNYAMİN", "PERVER", "ERDEM", "ÖMER", "SERHAT", "EMRE MERTER", "ABDÜLKADİR", "ENGİN",
			"NURULLAH", "ABDULLAH", "MEHMET", "HASAN", "AHMET MELİH", "ÖKKEŞ YILMAZ", "BURAK", "TOLGAHAN", "SÜLEYMAN",
			"SUAT", "ONUR", "MURAT VOLKAN", "OSMAN", "ALPER", "ERHAN", "AKIN", "ALİ", "ÖMER", "MUSTAFA", "FATMA DUYGU",
			"TÜRKER", "HASAN", "MUSTAFA TURAN", "ERGÜN", "ÖZLEM", "NİMET", "NİHAT", "SERHAT", "RAHMİ TUNA", "NEZİR",
			"YUSUF EMRE", "SERDAR", "RUKİYE", "HACI ÖMER", "ÇAĞLA", "ÜMİT", "MEHMET FATİH", "NAZIM", "HATİCE", "SİDAR",
			"MEHMET", "ALİ BURÇ", "YUNUS", "ÇAĞRI", "SİNAN DİNÇER", "AYSUN", "MUSTAFA", "İLKER", "PINAR", "MAHİR",
			"CEBRAİL", "RAMAZAN", "SELAHATTİN", "NEVİN", "MEHMET NEDİM", "YASİN", "BURHAN", "SEVCAN", "OSMAN SALİH",
			"MAHMUT EMRE", "TEYFİK", "SERTAÇ", "HALİM", "AYSU", "EMRE", "GİZEM", "ASUMAN", "GÖKÇE", "ZEHRA", "ORKUN",
			"SAVAŞ", "SELİM", "MURAT", "ERHAN", "FATİH", "ÖMER FARUK", "ZÜHRE", "AHMET", "ASENA", "EMRE", "İSMAİL",
			"MEHMET FURKAN", "HABİP", "İSMAİL", "ENDER", "HAKAN", "NURGÜL", "BURCU", "SELÇUK", "MAHMUT BURAK", "MURAT",
			"İLKER", "NİMET DİDEM", "NİHAL", "ŞÜKRİYE TUĞÇE", "REŞİT VOLKAN", "SELİM OZAN", "HASAN", "ZEYNEP", "SERKAN",
			"AHMAD TAHER", "HÜSEYİN", "ZAFER", "RAMAZAN", "SELKAN MURAD", "CANSEN", "KAMİL", "YÜCEL", "DERYA",
			"YASEMİN", "TOLGA", "ABDULKADİR", "ALİ", "ÖZEN", "FATMA", "MUSTAFA", "HAMZA", "MURAT", "CÖMERT",
			"HÜLYA GÖZDE", "BERK", "AHMET FURKAN", "YUSUF", "EMRAH", "EMRE", "ABDULSEMET", "EYLEM", "İSMAİL", "ÇAĞLAR",
			"YASEMİN", "MELTEM", "TOLGA CAN", "EMRE", "MUSTAFA", "EMİR MURAT", "FUNDA", "ELİF NUR", "NİLAY",
			"AHMET ÇAĞRI", "MERVE SETENAY", "SAMET", "ERDEM CAN", "SERKAN", "ÖZNUR", "ÖZNUR", "İSMAİL", "RAMAZAN",
			"FATİH", "ŞUAYIP", "ALİ", "SULTAN", "EMRAH", "GÜLBERAT", "UFUK", "ZEYNEP", "MUSA", "NURAN", "GÜLAY",
			"SERDAR", "CANAN", "AHMET", "HÜSEYİN", "ALPER", "CÜNEYT", "İLYAS", "HALİLİBRAHİM", "CANSU", "EMEL", "SEVAL",
			"EBRU", "MUSTAFA KEMAL", "MEHMET", "AYSU", "DUYGU", "ASLI", "NİHAL", "LEYLA", "BURÇİN MERYEM",
			"MAHMUT ARDA", "SERPİL", "AHMET KÜRŞAD", "EZGİ", "İPEK", "HAVVA", "ŞAHİN", "DENİZ ARMAĞAN", "GAMZE",
			"YAŞAR", "İLKAY", "SAADET NİLAY", "HÜSEYİN KALKAN", "TAMER", "HİDİR", "ADEM", "AHMET", "DERYA", "FATİH",
			"YAVUZ SELİM", "BEYZA", "ÜMİT", "AHMET", "FETHİ", "HİDAYET", "MEVLÜT", "YAVUZ SELİM", "MEHMET FATİH",
			"FETHİYE", "MERVE", "MEHMET KORAY", "EMİNE", "MELİKE", "MEHTAP", "SEVDA", "EMİN", "EMİR KAAN",
			"MEHMET HİLMİ", "HAVVA", "LÜTFİ", "EMİNE", "ELÇİN", "MUSTAFA", "GÖZDE GİZEM", "TEVHİD", "AYŞE GÜL", "FATMA",
			"TİMUÇİN", "BİROL", "MUAMMER", "DİLŞAH", "YAŞAR BARBAROS", "ELİF NİHAL", "YASEMİN", "ŞAHİN", "ALİ", "EMRAH",
			"AYŞE", "YAKUP ONUR", "ÇAĞRI", "BAHADIR", "KURTULUŞ", "KADİR", "MUSTAFA", "YALÇIN", "CEREN BUĞLEM", "ESRA",
			"ÜMİT", "ŞERİFE", "ŞEYDA", "ASLI", "NESLİHAN", "ESRA", "ÇAĞRI SERDAR", "MEHMET ALİ", "KAMURAN",
			"DERYA PINAR", "MERYEM", "ŞİRİN", "HARUN", "HARUN", "PINAR", "BARIŞ", "FATİH AVNİ", "İBRAHİM", "DAMLA",
			"MERVE", "ÖKKEŞ CELİL", "AYŞE", "RECEP", "EMRE", "HALİL", "MURAT", "SERKAN", "ALEV", "MÜŞERREF", "FATİH",
			"AHMET", "ADNAN", "HİSAR CAN", "ONUR", "ALP", "MUSTAFA", "ÇİĞDEM", "SERKAN", "ELİF", "HASAN", "AHMET",
			"BAKİ", "SEDA", "HANDAN", "AYLİN SEVİL", "NECMİYE GÜL", "ÖZLEM", "VOLKAN", "SERDAR", "EBRU", "ERTUNÇ ONUR",
			"EMRE", "PINAR", "MUHAMMED", "SONGÜL", "SÜMEYYE", "MEVSİM", "TUĞBERK", "UĞUR", "AHMET BURAK", "EMRE",
			"YİĞİT CAN", "FATİH MEHMET", "METE CAN", "ESRA", "FERHAT", "YELİZ", "SEVGÜL", "CÜNEYT", "SEYFULLAH",
			"HALİL CANSUN", "FATMA ESRA", "SERDAR", "YUNUS EMRE", "SEZAİ", "TAHİR", "VEYSİ", "YILDIRAY", "ELİF",
			"SERHAT", "ÖMER", "ÖZGE", "GÜLSÜM", "DENİZ", "SELİN", "GÜL", "BİLGE", "ŞEHMUS", "DİDEM", "MERVE", "EYYÜP",
			"NAZLI HİLAL", "HATİCE ÖZGE", "ABDULLAH", "MURAT", "HAYATİ CAN", "MERVE", "FERAY", "HASAN", "GAMZE",
			"HASAN", "RAMAZAN FERHAD", "BETÜL", "NESLİHAN", "MUSTAFA", "AYTAÇ", "SENA", "ZÜHTÜ BENER", "EMRE", "OSMAN",
			"BULUT", "ERSİN", "HALİL İBRAHİM", "AHMET", "SELÇUK", "NURMUHAMMET", "YASİN", "ÖZGE", "ZEKİYE SEVAL",
			"ERDEM", "DİLEK", "ŞULE", "GÖKNUR", "SALİHA", "VELİT", "MUHAMMED NURİ", "İBRAHİM", "TURGAY", "ENGİN",
			"HÜSEYİN", "MESUT", "LEYLA", "TUBA", "MEHMET ALİ", "SALMAN", "ŞEYHMUS", "ZEHRA", "NURDAN", "CEBBAR", "ÖMER",
			"AHMET", "SİNAN", "ALPEREN", "MEHMET SELAMİ", "ABDULAZİZ", "LEYLA", "FATMA", "MÜNEVER", "MÜRSELİN",
			"EMRULLAH", "EDA", "ÜMİT", "BÜNYAMİN", "RIDVAN", "ADEM", "ORHAN", "EMİNE", "KEMAL", "FATMA", "ALİ",
			"MEHMET", "MUSTAFA", "NEVZAT", "OSMAN", "ONUR", "MUSTAFA ALİ", "MEHTAP", "MURAT", "MUSA", "FİLİZ",
			"HALENUR", "KEZİBAN", "EFRUZ", "EMİN", "FATİH", "ÜNAL", "VOLKAN", "BAYRAM", "BERAAT", "MEHMET", "MÜCAHİT",
			"SABRİ SEFA", "RAŞAN", "RABİA ŞEBNEM", "AHMET", "AHMET", "ZÜBEYDE", "BÜŞRA", "BURCU", "ALİ", "IŞIL",
			"GÖZDE", "ESMA", "KÜBRA", "YAŞAR GÖZDE", "YILMAZ", "ZEYNEP", "CANER", "VOLKAN", "EMRE KAĞAN"});

	private static final List<String> SURNAME_LIST = Arrays.asList(new String[] {"ŞEN", "KANDEMİR", "ÇEVİK", "ERKURAN",
			"TÜTEN", "ÖZTÜRK", "YÜZBAŞIOĞLU", "VURAL", "YÜCEL", "SÖNMEZ", "ERTEKİN", "DEDE", "UYANIK", "ASLAN",
			"AKBULUT", "ORHON", "UZ", "YAVUZ", "ERDEM", "KULAÇ", "KAYA", "SELVİ", "AKPINAR", "ABACIOĞLU", "ÇAY", "IŞIK",
			"ÖZER", "ÖZDEMİR", "ÖZTÜRK", "TAHTACI", "BÜYÜKCAM", "KULAKSIZ", "AKSEL", "EROĞLU", "KARAKUM", "DAL", "ÖCAL",
			"AYHAN", "YİĞİT", "YARBİL", "CANACANKATAN", "GÜMÜŞAY", "MURT", "HALHALLI", "ULUÖZ", "ŞEYHANLI",
			"ÇALIŞKANTÜRK", "YILMAZ", "SARAÇOĞLU", "SEZER", "DOĞAN", "DEMİR", "KAYAYURT", "SÜRÜM", "YAVAŞİ", "TURGUT",
			"ŞEN TANRIKULU", "BARBAROS", "ALDİNÇ", "TEKİN", "GÜLŞAN", "KÜFECİLER", "ALMACIOĞLU", "ÇİLDİR", "TÜRKDOĞAN",
			"KAYA", "ÖNER", "ŞELİMAN", "YAMAN", "ATİK", "YİĞİT", "GİRAY", "YALÇINKAYA", "KILIÇ", "ŞENTÜRK", "KARABAĞ",
			"DEĞİRMENCİ", "BODUROĞLU", "YILDIZ", "GÜLER", "ERASLAN", "ÜZER", "PİŞİRGEN", "ADANIR", "KOÇ", "KORKMAZ",
			"YENİDOĞAN", "AYDOĞAN", "ACARBULUT", "ERGE", "ERDOĞAN", "ÖĞÜT AYDIN", "KUŞKU", "KUCUR TÜLÜBAŞ", "PEKTAŞ",
			"KAYACAN", "GÜLEN", "DOĞAN", "AYDIN BADILLIOĞLU", "GÜLEN AKKÜÇÜK", "CANDAN", "TEMEL", "YENİGÜN", "YILDIRIM",
			"BEDER", "AKINCI", "ÖZDEMİR", "ONUK", "AYDOĞAN", "YILMAZ", "AKCAN ATASOY", "SARAÇOĞLU ÇEKİÇ", "CÖMERT",
			"TOPAL", "KARAHAN", "ŞAHİN", "ÇETİN", "YILMAZ İNAL", "AYTAÇ", "YILDIZ ALTUN", "KİŞİ", "GÜNDÜZ",
			"ÖZKURT PÜRCÜ", "AK", "URFALI", "KARAMAN", "MEMETOĞLU", "KAZBEK", "KİREÇÇİ", "AKIN", "YADİGAROĞLU",
			"YÜKSEL", "ÖZÇELİK ORAL", "BABUŞ", "KAPLAN", "AKÖZ", "KARTAL", "BİLGİÇ", "ERDEN", "TUĞCUGİL", "KUMRAL",
			"ERBAŞ", "ORAL", "KILAÇ", "CENGİZ", "YILDIRIM", "KUTLUCAN BAĞCI", "BALABAN", "KAYA", "BALCI", "TÜFEKÇİ",
			"ATAY", "YARAR", "SEVER", "YILDIRIM", "ARSLAN KAŞDOĞAN", "ARKAN", "TUTAŞ", "ÖZTÜRK", "HAVAS", "SEÇİR",
			"YILDIZ", "SOYKAMER", "BEKTAŞ", "BERK", "GÜL", "GEDİK YILMAZ", "CENGİZ", "ÇOLAK", "BULUT", "SARI", "AKYOL",
			"BAĞCIK", "KUTLUYURDU", "DEMİRGAN", "YİĞİT KUPLAY", "GERİLMEZ", "DÜZKALIR", "KÖKSOY", "GÜLŞEN", "AKAR",
			"ÖZDOĞAN", "TÖNGE", "YASA", "ÖNVERMEZ", "YILDIRIM", "BİÇER", "KARADEMİR", "ALIMLI", "AKGÜL", "HANCIOĞLU",
			"BATÇIK", "OLPAK", "BOLAT", "ARSLAN", "SİĞA", "MERCAN", "BOZKURTER", "GÜLER", "ERGİNEL", "ŞAHİN", "KADAK",
			"GÜNEY KOCABAŞ", "GAYRETLİ AYDIN", "HEPKAYA", "BAYRAM", "KANIK YÜKSEK", "KULAK GEDİK", "AKCAN PAKSOY",
			"ESER", "KILIÇ YILDIRIM", "GİDER", "KURT", "ELLİALTI", "DEMİRTAŞ", "ARGA", "BAŞKAN VURALKAN", "ALUÇLU",
			"MUTLU", "ŞATIR ERTEM", "ENGİZ", "ÇİPE", "UYSAL", "BAŞER", "ARSLAN", "GÖZKAYA", "ULUTAŞ", "PİRİM", "ÜSTÜN",
			"KIZMAZOĞLU", "ULUBA", "ARSLAN", "KARAOĞLU", "ÖZSOY", "YALÇIN", "SAF", "VURAL", "DEMİRTAŞ", "GENÇPINAR",
			"AKASLAN", "UYĞUN", "ATAY", "ÖNDER SİVİŞ", "BAYMAK", "ATAY", "GÜVENÇ", "AKCA ÇAĞLAR", "ÖZCAN",
			"ERDEM ÖZCAN", "BAŞMAN", "YANNİ", "ÜNAL", "GÜNDOĞDU", "ÇELİK", "USTA GÜÇ", "TANRIVERDİ YILMAZ", "TAŞKIN",
			"ÇETİN", "YILMAZ ÇİFTDOĞAN", "GAZETECİ TEKİN", "SARİ", "KARAKOYUN", "KARAKUŞ EPÇAÇAN", "EKİCİ", "AYDINER",
			"AKTAŞ", "BELGEMEN", "ÇETİN", "OFLAZ", "BUĞRUL", "BAYSOY", "BÜKÜLMEZ", "YILMAZ", "BIÇAKÇI", "KARA",
			"TİMURTAŞ DAYAR", "ATEŞ", "BİNBOĞA", "KIZILTEPE", "KAYA", "ABSEYİ", "AMİROVA UÇAN", "ÖZTÜRK", "TAŞ",
			"CEYLAN", "KILIÇ", "EROL", "TAYFUN", "KAYA", "KARAKURT", "BUDUNOĞLU", "ÖZER", "SAYGIN", "ERYAVUZ",
			"POLAT ÇİÇEK", "YILMAZ", "ÇELİK", "ÜNSAL", "ALPINAR", "CİNDEMİR", "AKDUMAN", "UYAR", "TÜLPAR", "AZAK",
			"EREN", "GÖZCÜ", "BAYSAL", "TUNCEL", "ÇETEMEN", "YILMAZ", "GİNİŞ", "UZUN", "NASIROĞLU", "SEZGİN", "ÖZTÜRK",
			"YILDIRIM", "UZUN", "BULUR", "DUYSAK", "YENİN", "DEMİREL", "SAK", "KOCABAŞ", "SARAÇ", "ALKURT KAYIKÇI",
			"YURT", "İLKAY", "TAVŞAN", "ALAY", "ERTEM", "ÖZEL", "GENÇ", "UĞUZ", "EVİK", "GENÇ TALAS", "EKER", "ÇİMEN",
			"ÇIRAKOĞLU", "DEMİR GÖÇMEN", "ALPAYCI", "AK", "ÇELİK", "ERCAN", "ALTUN", "KILIÇ", "SARP", "SÖKER", "KÖSE",
			"BARÇAK", "ÖZEKLİ MISIRLIOĞLU", "BOLAÇ", "ASLANALP", "ÖRNEK", "AKDOĞAN", "ÖZÇELİK", "ERTÜRKLER", "SARAL",
			"ÖZKAN", "DEMİRHAN", "ASLANKARA", "EMLAKÇIOĞLU", "ÖZTÜRK", "ESER", "ÖDEN", "DEMİRAY", "AYHAN", "YAĞCI",
			"AVCI", "BAYGELDİ", "BÜKÜM", "DİNCER", "DOĞAN", "EKİZ", "ŞAHİNER", "ŞENGÜL", "İLGÜN", "AŞIK", "ÖZKAN",
			"ŞİRZAİ", "ÖCALAN", "KABA", "TÜLÜCE", "AYTEKİN", "KAYA", "DÜGER", "METİNEREN", "BULUT", "ŞAHİN DUYAR",
			"ÇETİN", "BÖLÜK", "GÖZAÇAN", "BOZKURT", "ÖNEY KURNAZ", "AY GÜNEY", "KÖYLÜ", "ÖZMEN SÜNER", "TALAN", "DUMLU",
			"ZORLU KARAYİĞİT", "KÖYCÜ", "UYGUR", "KABACAOĞLU", "TOPALOĞLU", "AYIK", "DEMİR", "ERDEM", "KARAMANLI",
			"SADİ AYKAN", "OKTAY", "YURTLU", "SALMAN SEVER", "CİRİT KOÇER", "SORGUN EVCİLİ", "NURÇİN", "BAŞKAN",
			"KAZANCI", "KIYAK YILMAZ", "METE", "UZUN", "SAĞDIK", "ARIKAN YORGUN", "EKİCİ", "KESİM", "GÜL", "YILDIRIM",
			"KARAGÖZ", "PEKEL", "YAKAR", "TARLAN", "ÇATAK", "ÇETİNKOR", "SAYIN", "KURT", "GÜMÜŞ", "KOCAKAYA ALTUNDAL",
			"ÖZMEN", "GÜNAY", "DÜZ", "DİLEK", "DEMİRTAŞ", "KURTULUŞ", "KARPUZOĞLU", "ERGİNTÜRK ACAR", "BEYOĞLU",
			"SULHAN", "ARSLAN", "NUHVEREN", "AVCIOĞLU", "AHISKALI", "ASENA", "KARACAN ERŞEKERCİ", "GÖLEMEZ", "YILDIRIM",
			"TOHUMOĞLU", "ÇELİK", "BOZARSLAN", "KÖŞKER", "ÇELİK", "SÜL", "KORKMAZ", "TARKAN", "DUMAN", "HODJAOGLU",
			"BALLI", "ŞATIROĞLU", "ÖNDE", "LAĞARLI", "ÖZÇAY", "ARSLAN", "AKDEMİR", "İÇBAY", "AKIN", "DEMİRÖZ", "KUYUCU",
			"SELÇUK", "İNCE", "ERGÜLÜ EŞMEN", "GÖKALP", "BABACAN", "ÜLGER", "AYVAZ", "ELVERDİ", "AYDIN", "DEMİREL",
			"CİMBEK", "FIRAT", "BAHÇEBAŞI", "DAM", "KÖROĞLU", "ÖZÇELİK", "KARACA", "SEVEN", "ÖZKURT", "ALTUN",
			"BÜYÜKTAŞ", "SERTKAYA", "ÖVEN USTAALİOĞLU", "YALNIZ", "SAVAŞ", "YILMAZ", "YALÇIN", "BEREKET", "KAYA",
			"PEKGÖZ", "DEMİR", "OLMAZ", "SEVİNÇ", "MERHAMETSİZ", "ÇOBANOĞLU", "ŞİMŞEK", "BİNNETOĞLU", "ÖĞÜTMEN KOÇ",
			"ÇINKIR", "CAMCI", "YAZAK", "NİZAM", "TÜRKOĞLU", "DEMİRKOL", "AKSAKAL", "AKIN", "BOZOĞLAN", "DEĞİRMENCİ",
			"AYMAN", "SAÇLI", "KARAKILIÇ", "BAKANAY ÖZTÜRK", "KARAKÖSE", "GÜVEN MEŞE", "YEŞİLOVA", "EŞKAZAN", "GERDAN",
			"MUMCUOĞLU", "VATANSEVER", "PAKÖZ", "ATMIŞ", "AKÇALI", "FAKIOĞLU", "YENİDÜNYA", "ANIK", "KÖSEOĞLU", "SONAY",
			"ÇELİKER", "ÖZDEMİRKIRAN", "ÇELİK", "KÖSE", "AKIN", "DURÇ ÖZTÜRK", "İNER KÖKSAL", "BEREKATOĞLU", "DİLLİ",
			"ELBÜKEN", "BAHÇECİ", "BÜLBÜL", "KADI", "IŞIK", "YÜCETÜRK", "BULUR", "ÖZİŞ", "ULUBAŞOĞLU", "AKŞAHİN",
			"KARPUZ", "YABUL", "GÖKSOY", "ÜNAL", "IŞIK", "KÖKSAL", "TEKİŞ", "AKSOY", "BAŞYURT", "YURDSEVEN", "ERDEM",
			"MERDEN", "KISA KARAKAYA", "SANHAL", "ŞAHİN", "VATANSEVER", "BİLGİ", "KAYABAŞ", "GÜRBOSTAN", "BOLAT",
			"KABİL KUCUR", "DEĞİRMENCİ AKAR", "TAYYAR", "ŞAHBAZ", "YANCAR", "OLGAÇ", "EKİZ", "EREN", "MALÇOK", "KARASU",
			"KARADAĞ", "TOPRAK", "SAĞLAM", "ŞAHİN", "KEBAPCILAR", "TATAR", "ARSLAN", "YÜCE", "TOLA", "GÜNGÖR",
			"KARAGÖZ", "ALTINBOĞA", "YENİÇERİ", "IŞIKALAN", "ÖZDEMİR", "GÜRBÜZ", "KURU", "YURDAM", "KARA", "ÇETİN",
			"BAŞARAN", "ŞAHİN", "ÜREYEN", "IŞIK", "ÖZTÜRK", "DOĞAN", "MESCİ HAFTACI", "ORHAN", "VURAL", "EROL",
			"BALSAK", "ÖZDEMİR", "ÇİFT", "ŞEN", "YAZICI EROL", "BAYRAMOĞLU", "GENÇDAL", "DESTEGÜL", "ÖZDEMİR", "KARÇİN",
			"ASLAN", "BAZ", "ALTUNTAŞ", "ÖZCAN", "KIRBAŞ", "YILMAZ", "KAYMAN KÖSE", "ÇETİN", "YEŞİLDAĞER", "YÜKSEL",
			"KAYAOĞLU", "KILIÇ", "CELTEMEN", "GÜNDÜZ", "ŞANLIKAN", "ÇELİK", "ORHAN", "TANTEKİN", "KARAALP", "TUNCER",
			"ATASOY", "DOĞAN", "GÖK", "ÖZDEMİR", "ÖZKAN", "ONAR ŞEKERCİ", "ASOĞLU", "KHALİL", "YURDAKÖK", "YILMAZ",
			"KESKİN", "KOÇARSLAN", "GÖKALP", "TUNÇAY", "SÜRMEN AKYOL", "AYDIN", "SEVİNGİL", "GENÇPINAR", "AKKAYA",
			"KUSERLİ", "BOZKURT", "ŞİMŞEK", "YILMAZ", "TALAS", "ÇEVİKER", "KAYNAK", "BAYRAKTAR", "ÇETİNTAŞ", "CANTÜRK",
			"KARADENİZ", "ALAN", "KOYUNCU", "KARTAL", "KAYA", "BAKIRCI", "NAR", "ULUSOY", "CELİLOĞLU", "YÜCEL", "DUMAN",
			"AKDENİZ", "YERAL", "GÜRDAL", "KÜTÜK", "KANYILMAZ", "ÖZBEK", "UYSAL", "AKIN", "AKDENİZ", "KAPLAN",
			"ALBAYRAK", "YILDIRIM", "MÜEZZİNOĞLU", "AYHAN", "UYGUR", "TÜFENK", "YÜCEL", "DEMİR", "ÖZDEMİR", "KIRIŞ",
			"KIRASLAN", "SALTÜRK", "AÇIKGÖZ", "YAĞCI", "SEVÜK", "KAYA", "DOĞAN", "YILDIZ", "BAYTAN", "DEMİRTAŞ",
			"MUTLU", "GENÇ", "AKTUĞ", "SERİN", "TUNCAY", "GÜNBEY", "KAYA", "TAŞAR", "AVSEREN", "BAL", "BATMAZ",
			"VEZİROĞLU BİRDANE", "ARAZ SERVER", "GÜLER", "DALYAN CİLO", "KARADAĞ GEÇGEL", "BAŞTÜRK AYHAN", "ERSOY",
			"TAY", "ERYILMAZ", "DEMİR", "AYDIN", "OCAK", "BÖLÜK", "ÖZMEN", "ÖZTÜRKERİ", "EKEN", "AKGÜL", "SARICA DAROL",
			"CANSEVER", "AKIN", "GÜZEL", "ÖZER ÇELİK", "ÇAKIR", "AKSUN", "BALAL", "BAYAM", "ŞAİR", "ÜNLÜ", "YUMURTAŞ",
			"AKGÜL", "AYKAN", "ALPSAN GÖKMEN", "CANATAN", "MUMCUOĞLU", "TAŞKIRAN", "HATİPOĞLU", "AKYOL", "SUCAK",
			"YILDIZ", "AKPINAR", "GÖKSEL", "KARSLI", "ÖZGÜROL", "ACAR", "KALEM", "ŞAHİN", "AYDIN", "DÖKMECİ", "GÖRMELİ",
			"ÖZATEŞ", "SERVET", "TOPRAK", "SÜNER", "SARIKAYA", "SULUOVA", "SERBEST", "EFE", "TOPAK", "ATBİNİCİ",
			"KIYAK", "ÇELİK", "ÖZ", "TEPE", "ÖZÜAK", "ÖNCEL", "CANBAZ", "AL", "DEMİR", "GÜRER", "GÜNGÖR", "GÜZEL",
			"GÖNCÜ", "ÖZDAMAR", "KARATOPRAK", "ÇAVDAR", "KARA", "ÖZ", "SÖZEN", "GÖKÇEK", "KARAKAYA", "GÜNGÖR", "ÇEPNİ",
			"KIR", "ERSOY", "ÇAĞLAR", "ÖZALP", "EVRENOS", "BAYRAKTAROĞLU", "USLUSOY", "SARI", "ATALAY", "TOPKARA",
			"BEKTAŞ", "TENEKECİ", "ÇAĞIL", "MERTOL", "TAŞ", "HIDIROĞLU", "ŞEN GÖKÇEİMAM", "KARAHAN", "ÖNAL MUSALAR",
			"DEMİREL", "YACI", "IŞIKLI", "KILIÇ", "ÜLGEN", "KÜÇÜKGÖNCÜ", "SU KURT", "KOÇAR", "BALOĞLU", "DUMAN",
			"ASLAN", "SARICANBAZ", "SERT", "ALTUN", "GÖRMELİ", "YILMAZ GELEBEK", "AKYOL", "ÖZAN SANHAL", "AKYILMAZ",
			"BAKAN", "KARAKAN", "GÖRKEM", "CILIZ BASHEER", "KARACAN", "TEN", "ATLANOĞLU", "ÖZTÜRK", "TOPALOĞLU",
			"SOYDAN", "TÜRKAY", "MENTEŞ", "PINARBAŞILI", "ONAY", "CERİT", "ÜNAL", "ALTUN", "YILDIZ", "İMAMOĞLU",
			"ÖZDEMİR AKDUR", "YANMAZ", "BÜBER", "AKKAYA", "BAKAN", "TAŞMALI", "BULAKÇI", "BAYRAM", "AYDIN", "GERGER",
			"YEŞİLKAYA", "DÖNMEZ", "YILMABAŞAR", "DİKİCİ", "ARİFOĞLU", "FİDAN", "SAKARYA", "ÖZEN", "ONAN", "AKHUN",
			"KIR", "ŞAHİN", "SU DUR", "YAZICI", "GÜRDEMİR", "ALTINSOY", "KALYONCU UÇAR", "ŞAŞMAZ", "GÜLCAN",
			"KURT GÜNEY", "ÖZTÜRK", "ULUTAŞ", "ALTUNA", "GÜREL", "KARAKUŞ", "KILIÇ", "ÖZKIRIŞ", "KAYA", "YILMAZ",
			"İNCİ KENAR", "DEMİR", "AKBAŞ ÖNCEL", "EREN", "BİCAN", "AYDIN", "ÖZDOĞAN KAVZOĞLU", "ATEŞ BUDAK", "KÖKSAL",
			"SARGIN", "AKKOYUNLU", "ŞİMŞEK", "ÖZTÜRK", "KAYHAN", "TEZER", "KARACAN ", "ÇAKIR", "UYSAL", "GÜRAKAN",
			"DOKUMACIOĞLU", "KIRHAN", "ÖZDEMİR", "KAYA", "GÜL ÖZMEN", "ESEN", "AK YILDIRIM", "EKER", "ÖZAVCI AYGÜN",
			"ÇEKİÇ", "SAVRAN", "GÖKALP", "GÖKMEYDAN", "EMRE", "KÜTÜKCÜ", "DİKOĞLU", "AKSOY", "GÖRENEKLİ", "KOCA",
			"KILINÇ", "BATGİ AZARKAN", "TÜRKMEN ALBAYRAK", "OKULU", "KİRİŞCİ", "DEDE", "KIZMAZ", "ARGON", "ALICI",
			"ARIKAN", "FINDIK GÜVENDİ", "ÜÇER", "ÜNAL", "YILMAZ", "ÇETİN", "ERGÖZ", "FİLİZ", "ALABALIK", "KIZANOĞLU",
			"YAŞAR", "ÇELEN", "DEMİRELLİ", "DOĞAN", "DÖNMEZ", "AKDEMİR", "DANIŞOĞLU", "GÜRSOY", "ŞENER", "ABAT",
			"ERGÜN", "ÇİÇEKBİLEK", "ÜNÜŞ", "OĞUZ", "KOL", "TOKER", "SU", "POLAT", "KELEŞ", "SEYREK", "ÖZKAN", "ASİL",
			"TOKTAŞ", "ARDIÇ", "ÖZDEMİR", "SÖZEN", "ÇOBANYILDIZI", "MÜFTÜOĞLU", "YEGEN YILMAZ", "AKSOY", "AKYOL",
			"OFLAZ", "KARAOĞLANOĞLU", "AYAS", "KARKIN", "BİLİCİ", "ALBAŞ", "GÜNDÜZ", "TURAN", "DEMİR", "BAYRAKÇI",
			"DÖNMEZ", "GÜRSOY", "ÖZER", "AŞKIN", "KASAPOĞLU", "SARI", "YALÇIN", "ÖZTÜRK", "BARIŞAN", "ARDA", "BARDAKÇI",
			"ULU", "KIVRAK", "BULUT", "KILAVUZ", "TAN", "CANDAN", "EŞFER", "KARAKULAK", "BABAYİĞİT", "BARAN", "ÖZYÖRÜK",
			"DENİZ", "ÖZDEN", "KARAPIÇAK", "ÖZDEMİR", "SERT", "SAKA", "YILMAZ", "NASUHBEYOĞLU", "İPLİKÇİ", "ÖZEK",
			"KAYA", "AYYILDIZ", "POLAT", "ÇELİKTEN", "SERTOĞLU", "SARAYLI", "KILIÇOĞLU", "SEZEN", "KOCABIYIK",
			"ATILGAN", "DARAMAN", "ÇİMEN", "KESKİN", "ALTAŞ", "YAVUZ", "GÖKGÜL", "KAHRAMAN", "GÜNGÖRMEZ", "TAŞDELEN",
			"EMELİ", "KILIÇ", "SINICI", "BULUT", "GÖKTAŞ", "BAŞAR", "KOÇ", "ÜNAL", "TÜRKSOY", "BAYRAK", "SEVİNÇ",
			"ÇOKER", "KUMBUL", "ÖZATA", "SEYREKOĞLU", "BOLATTÜRK", "DİRİCAN", "YILMAZ", "TÜREMENOĞULLARI", "AYDEMİR",
			"DEMİR", "APAYDIN", "DEMİR", "YILMAZ", "DENİZ", "KOYUNCU", "SAĞCAN", "EVMEZ", "AKBULUT", "KÖKSAL", "KARA",
			"ÖZCAN", "DEMİREL", "TİKİCİ", "TOMBUL", "ÇİMEN", "ÇİFTCİ", "KELERCİOĞLU", "BÜYÜKKAL", "BAL", "KOÇ", "AÇAR",
			"KARADAVUT", "DEMİRTAŞ", "ÖZEL", "DUMLUDAĞ", "AĞCA", "AKGÜL", "BAYRAM", "BEKLER", "CEYLAN", "KOÇER", "OKUR",
			"GENEZ", "KAYA", "KILINÇ", "GÖRMÜŞ", "DOĞAN", "ÖZLÜ", "YILDIZ BULUT", "PULAT", "SOBAY", "COŞKUNER",
			"PEYNİRCİ", "CAN", "KILINÇ", "ÇİÇEK", "ORAL", "İNCEOĞLU", "KAYA", "SÜMER", "DUYAN", "KARAMEŞE", "KAPAR",
			"PAKSOY", "KALIPCI", "DÜZCE", "ÖZTAŞ", "YILDIRIM", "ÇELİK", "BİÇER", "CANDAN", "GÜLBAHAR", "TURAN", "ŞAHİN",
			"DOĞAN", "ŞAHİN", "DÜZGÜN", "ŞENCAN", "AKTEMUR", "ÖZMEN", "TOY", "KARADURAK", "AKTÜRK", "ÖZKAYNAR",
			"GÜNEYSU", "AYDIN", "FİŞEKCİ", "ESKİMEZ", "ŞANLIER", "SELVAN", "YÜREK", "EREL", "TÜRKAY", "UTLU", "YILMAZ",
			"VURAL", "YILDIRIM", "ÖRDEK", "KULAKSIZ", "COŞKUN", "FİLİZAY", "HASSAN", "YAMAN", "GÖÇEN", "GÖZÜGÜL",
			"SÖNMEZER", "TAŞKIRAN", "GÜNEBAKAN", "OCAK", "BİRİ", "YILMAZ", "KAYA", "GÜVEN", "GÜNEYSU", "GÜZELKÜÇÜK",
			"ATBAŞ", "SEYHAN", "DOĞAN", "AKKAŞ", "DİL", "GÜVEN", "DENİZ", "BALTACI", "ÇİÇEK", "SEVEN", "TAN", "BARAN",
			"POSTALLI", "BULUT", "ARMAĞAN", "SOLUK", "BAL", "KAYA", "TÜRKAY", "CORUT", "ÜNÜVAR", "ATAY", "KIRAÇ",
			"AKOVA", "IŞIK", "ÖZEN", "TOPALKARA", "BOZPOLAT", "SEVER", "GEDİKLİ", "ŞAHAN", "KUŞAKÇI", "MANAV",
			"KARABULUT", "AKBAŞ", "CANBOLAT", "ŞAHİN", "KARADAĞ", "BABA", "EFE", "SARI", "SERTKAYAOĞLU", "UÇAR",
			"ZEYLİ", "YILMAZ", "YENİLMEZ", "KURTULUŞ", "ÖRENÇ", "AKGÜL", "ÖZGÜR", "ÇİNPOLAT", "KUŞ", "DURSUN",
			"YARADILMIŞ", "GELEGEN", "ÖLKER", "KARA", "GÜLŞAN", "KAPLAN", "ÖZTOPRAK", "ÖZMUK", "KAVUKOĞLU", "KINDIR",
			"KEMİK", "SÖKMEN", "KAYRA", "İPEK", "MERCAN", "KEVKİR", "HAMDEMİRCİ", "YILMAZ", "EROĞLU", "KARAKAYA",
			"ŞAHİN", "ERAT", "TANİN", "YEL", "TAŞTEKİN", "ATİLLA", "OZAN", "KARADUMAN", "TAZEOĞLU", "DURMUŞ", "AZRAK",
			"ERZURUM", "KARADEMİR", "MELEKOĞLU", "HALİS", "CANDANER", "ALTUNÖZ", "GAZİ", "ARSLAN", "İNSELÖZ", "KOCA",
			"YEŞİLKAYA", "ÜNAL", "TAŞ", "UYSAL", "DEVECİ", "ACAR", "SİL", "ÜNGÜR", "KÜTÜKOĞLU", "EROĞLU", "ERDOĞAN",
			"TUNÇ", "GÜMÜRDÜ", "KASKALAN", "OLCAR", "ÇAKMAK", "ALBAY", "AYDIN", "DOĞAN", "ALBEN", "AVCI", "AK", "ÖZBEK",
			"TEMURTAŞ", "TURAN", "ÜÇGÜL", "EKİNCİ", "ERDOĞMUŞ", "ALTAŞ", "KAÇIRA", "KANKILIÇ", "OYNAK", "YAKUT",
			"BERENT", "SUZAN", "KARDAŞ", "KAYA", "ASLAN", "KANKILIÇ", "YILMAZ", "ÜZEN", "GÜLLÜ", "SERT", "DEMİRER",
			"ÇAKMAK", "TEL KANKILIÇ", "AKBUDAK", "KAPÇAK", "KARADENİZ", "DEMİR", "ÖZSAYIM", "ÇETİN", "DİLMEN", "BAŞAK",
			"ISSI", "TURĞUT", "ÇELİK", "ÇOBAN", "SUVARİ", "AKAY", "YEŞİL", "ÇELİK", "YILDIZ", "BARAN", "HOŞER", "SAYIN",
			"TAYFUR", "SABAZ", "AVCU", "SUBARİ", "ÖZDEMİR", "YAKUT", "YÜKSELMİŞ", "ESEN", "BAKAY", "ÇİÇEK", "YAVUZ",
			"YILDIRIM", "HÜSEYNİ", "CAN", "GÜLER", "İZGİ", "YILDIZ", "AKTAN", "DÜZEL", "DEĞİRMENCİ", "TOKAR", "ÇEÇEN",
			"ÖZTÜRK", "BURULDAY", "ÇETİNKAYA", "ZENGİN", "KARAKUŞ", "KAYAALP", "KARDAŞ", "ÇOBAN", "YALÇIN", "SÖZER",
			"SAVAŞ", "ÖZER", "KURALAY", "KILINÇ", "ARACI", "ALP", "ALIM", "YÖRÜK", "YAVUZ SEZGİN", "TAŞ", "SEZGİN",
			"LEVENT", "KARAKAŞ", "FİLİZ", "DADALI", "AYDEMİR", "ARIKBOĞA", "AKYÜREK", "GÜNDÜZ", "GENCAN", "YILDIZ",
			"KITAY", "KORKUT", "İVACIK", "YILMAZ", "ÇATUK", "KARAKOYUNLU", "CİNBİZ", "CAMGÖZ", "BAŞDAŞ", "HAKYOL",
			"KILIÇ", "ŞEN", "VAROL", "SAĞ", "ÜRÜN", "COŞKUN", "BERBER", "ERDOĞAN", "AKMEŞE", "GÖKCE", "SEVİNÇ", "ŞAHİN",
			"HEYBET", "CEYLAN", "KURT", "KARABULUT", "BABADAĞI", "ŞİRZAİ", "ERTÜRK", "KADAK", "ALTIN", "PARLAK",
			"CEBECİ", "GÖKÇEK", "BAYANA", "BEKAR", "DEMİRTAŞ", "EROĞLU", "VURGAN", "KURTPINAR", "EROĞLU", "BAĞCI",
			"YAZICI", "SARIÇİÇEK", "TIRAŞ", "ÇİTİL", "OĞUZ", "KARA", "KOÇYİĞİT", "KÖKSAL", "BARATALI", "YORGANCI",
			"DEMİR", "ORAK", "UZUN", "KÖKSAL", "SÖNMEZ", "YILMAZ", "KILIÇ", "BAYRAK", "TOKATLIOĞLU", "ŞAHİNER",
			"YERLİKAYA", "EMİRLİOĞLU", "GÖKÇE", "BİÇER", "USUL", "TURAÇ", "GÜVEN", "ARIK", "GÜNDÜZ", "GÜLTEKİN", "TUNÇ",
			"ÖZKAN", "ÜNAL", "KANNECİ", "TEOMAN", "BAKLACI", "BARAN", "SEVİMLİ", "SÖYLER", "SARIKAYA", "KONURALP",
			"DÖNERTAŞ", "GÖKÇEN", "KARATAŞ", "BAŞARGAN", "KAHRAMAN", "TOKAT", "AYDIN", "GÜNDÜZ", "BARAN", "DEMİREL",
			"ÇAVLI", "ATAK", "BÖCÜ", "CÖMERT", "ÖREN", "YILDIRIM", "ŞENGÜLEROĞLU", "AKKAYA", "İLHAN", "YAYIKÇI", "KOR",
			"ŞİRİN", "KURT", "AKDEMİR", "YAŞAR", "DURUK", "ŞAHİN", "AVŞAR", "ÖZASLAN", "HÜSEYİNOĞLU", "BOZKURT",
			"KÜÇÜKYILDIZ", "MENEKŞE", "TÜRE", "OFLAZOĞLU", "AZAP", "ZAİM", "DURAN", "AYDUĞAN", "KOTTAŞ", "BACAK",
			"AVCI", "AYCAN", "ÖZCAN", "ŞAHİN", "1", "1", "TOPÇU", "KARLI", "ÖZDEMİR", "EKEN", "KURT", "GÜRPINAR",
			"CÖMERT", "AK", "KAÇER", "KANDEMİR", "AYDIN", "ONRAT", "ELÇİÇEK", "YAKAR", "ADIGÜZEL", "ÖNDER", "TEHLİ",
			"OSMANCA", "USLUBAŞ", "GÖRÜNMEZ", "DAĞ", "OLGEN", "ATEŞ", "ÜZÜM", "ÖZCANLI", "ŞEKERLER", "BAKLACI",
			"ÇINKIT", "BAŞCI", "KESGİN", "GÖDE", "ŞENTÜRK", "BİLGİÇ", "ÇETİN", "ŞARLAK", "KERİMOĞLU", "SELİM",
			"YILDIZOĞLU", "BULUÇ", "DEDE", "YAVUZ", "KÖROĞLU", "KOCUBEYOĞLU", "KARALAR", "ERDOĞAN", "ÇOPUR", "EYİSOY",
			"ÜSTÜNDAĞ", "KILAVUZ", "YÜCEL", "ÖKTEM", "YAŞAN", "USLU", "BOZOĞLU", "GÜLTEKİN", "KOCASARAÇ", "DURAN",
			"ALĞAN", "ERDAL", "KAYKA", "BACAKSIZ", "ÜNALAN", "CAN BORAN", "ZEYBEK", "KILIÇ", "TEMİZ", "KARAKAŞ", "OĞUZ",
			"AYIK", "ETLEÇ", "PEHLİVAN", "COŞAR", "KAHYA", "ÇETİN", "ATALAY", "YALÇIN", "UFACIK", "DURMAZ", "TURMUŞ",
			"ÇİLESİZ", "CANPOLAT", "ÖZDEMİR", "KORUCU", "ÇOBAN", "TUNCER", "YALÇIN", "GÖKTAŞ", "DEMİRDAĞ", "KAYA",
			"POLAT", "GÖZCÜ", "ÇELİK", "BOZAN", "KAYA", "İNANMAZ", "AKSÖZ", "KURT", "BAŞARAN", "DEMİRTAŞ", "TUNÇ",
			"YAŞİT", "ÇELİK", "ÖZDEMİR", "GÜMÜŞÇÜ", "GÖKKOCA", "DEMİRKAPI", "KARACA", "ÇEVİRGEN", "DÜNDAR", "KARAGÖZ",
			"ŞAHAN", "ELÇİ", "ATİLA", "ALBOĞA", "AKTAŞ", "ASIG", "AKKAN", "AYDIN", "ATEŞ", "ATABAY", "BALTA", "AYDIN",
			"BÖRTA", "BOĞAN", "BAYAR", "DEMİR", "DAL", "ÇÖREKLİ", "HALICI", "GÖKTEPE", "KOLUŞ", "KAYA", "İNANÇ",
			"ÖZDEMİR", "ORMAN", "NACAR", "SADIÇ", "POLAT", "ÖZKANLI", "ÖZEL", "ŞENCAN", "ŞAŞMAZ", "SALDIRAY", "ŞAHİN",
			"SULTANOĞLU", "SOMAY", "POLAT", "ŞİRİN", "URUÇ", "TOLU", "SÜVERAN", "SÜMER", "YILMAZ", "YILDIZ", "YILDIZ",
			"YETKİNŞEKERCİ", "BAYRAM", "SOYTAŞ", "BOLSOY", "BİLİCİ", "AYDOĞAN", "ARPAT", "ALIŞIK", "AKDEMİR", "ŞAHİN",
			"ONAT", "KARAARSLAN", "GÖKTEN", "EVRAN", "ERASLAN", "DEVECİ", "ARARAT", "AKANSEL", "ULU", "TOY", "YILMAZ",
			"DEVELİ", "DEMİRCAN", "BÜYÜKDOĞAN", "GÜLEÇ", "ERDEM", "EMET", "İLERİ", "İLBAY", "IŞIK", "GÜNEŞ", "GÜNDOĞDU",
			"GÜLENAY SÜMER", "NADASTEPE", "ÜLKEVAN", "ŞİMŞEK", "SEĞMEN", "ÖZKAN", "ÖZDİL", "MERT", "MERSİN",
			"KIRLANGIÇ", "KENDİRLİ", "KARNAS", "KARCIOĞLU", "KARAİSMAİLOĞLU", "KARAHAN", "KARADENİZ", "KAN", "İNCE",
			"YANIK", "YAĞCIZEYBEK", "TUNCER", "TOŞUR", "ŞAHİN", "SÖNMEZ", "SOYER", "SEZER", "SANDAL", "ÖZDEMİR",
			"ORDULU ŞAHİN", "OMAY", "KÜTÜK", "KOYUNCU", "KORKMAZ", "KIRCALI", "KARAAĞAÇ", "KABAKUŞ", "İLGEN", "GÖRDÜK",
			"GÖKTAŞ", "EVREN", "ERTAŞ", "ELDEM", "DOĞAN", "DİRİCAN", "DEMİRTÜRK", "DEMİR", "ÇELEN", "ÇAĞLAR", "BAŞAR",
			"BAŞ", "BARÇA", "BALCI", "AYDOS", "İLBAY", "BOBUŞOĞLU", "KIRLI", "AÇAR", "BÖYÜK", "YURTDAŞ", "YILDIZ",
			"YEGİN", "YAKKAN", "ULAŞ", "TUNÇ", "TOPER", "TEKİN", "ŞAHİN", "ŞAHİN", "SAYİNER", "SARIGÜL", "ÖZSAYGILI",
			"ÖZDEMİR", "KOLSUZ", "KIRLI", "KILINÇ", "KEŞKEK", "KEKLİKÇİOĞLU", "KAYKI", "KARAKÖK GÜNGÖR", "KACEMER",
			"İŞGÖREN", "İNCE", "ILGIN", "HİÇDURMAZ", "GÜVERCİN", "ESMERAY", "ERKAN", "EREL", "EMLEK", "DAYLAK", "ÇOMUT",
			"ÇELİK", "BOZKURT", "BOSTANKOLU", "BAHÇECİ", "BAĞ", "ALPARSLAN", "ALADAĞ", "AKDEMİR", "AKÇAYIR", "AKÇAY",
			"ZUBAROĞLU", "YILMAZ", "YILMAZ", "YETER", "YARDIMCI", "ÜNVER", "TÜRKYILMAZ", "TUTAR", "TEPE", "TAŞCI",
			"ŞAHİN", "SUSAM", "SÖZEN", "SİPAHİ", "SAVRAN", "SAVRAN", "POTA", "POLAT", "ÖZTÜRK", "ÖZDEMİR", "ÖZBEY",
			"OKÇU", "NURLU", "KURT", "KILINÇ", "KILIÇ", "KAPLAN", "KAPLAN", "KAMACI", "KAHRAMAN", "İNAN", "GÜNAYDIN",
			"EZER", "EŞME", "ERSOY", "ELMAAĞAÇ", "DADLI", "ÇERİK", "COŞKUN", "COŞKUN", "BÜYÜKPASTIRMACI", "BÜRKÜK",
			"BOLKAN", "BAYHAN", "BAHADIR", "BAĞCI", "BAĞ", "AYKOL", "AYDIN", "ASİL", "ARSLANOĞLU", "ARSLAN", "ALAN",
			"AKKOYUN", "KİRAZ", "BÜYÜKBULUT", "IŞIK", "CENGİZ", "GÜNEŞ", "GÖK", "ALTAŞ", "KÖKSAL", "TANRİVERDİ",
			"YILMAZ", "BERTAN", "BAK", "IŞIK", "ÖZER", "DEMİRCAN", "ÖZAYDIN", "SELVİOĞLU", "BİLGİÇ", "KOÇ", "ARSLAN",
			"BAZİKİ", "KÜÇÜKÖZKAN", "SÜRÜCÜ", "UYKUR", "SUMAN", "KAYA", "TACİR", "ATEŞ", "TANRIKULU", "ÖZGÜNER", "KULA",
			"ERDOĞAN", "GEZİCİ", "ANIK", "NART", "BAKLALI", "MAĞATUR", "DİNLER", "ŞİMŞEK", "GENÇER", "ŞAHİN", "VARICI",
			"SEZGİN", "KINIK", "PAKSOY", "KALKAN", "ÖZTÜRK", "DAYANGAÇ", "KAZAN", "KOZANOĞLU", "ZİLELİGİL", "TAŞCI",
			"DEMİR", "SAĞIR", "BAŞKAK", "ÇELİK", "TOPTAŞ", "AKPINAR", "KIZILDAĞ", "KÖSEOĞLU", "ELİK", "DEMİR",
			"ALBAYRAK", "YALÇİN", "ÖZTAŞ", "DENİZ", "YILDIZ", "ŞAHİNER", "ÇETER", "NACAROĞLU", "DENİZ", "KAVMAZ",
			"YILDIRIM", "ERKAN", "ATEŞ", "BOZAN", "BAŞYİĞİT", "1", "KELEŞ", "ERDOĞMUŞ", "ÇAKMAK", "BİLAL", "SUNGAR",
			"KILIÇ", "ELÇİ", "KOCAMAN", "TAŞKIRAN", "KUTLAR", "ÇİFTCİ", "CANDEMİR", "AYKUŞ", "ÇELEBİ", "YILMAZ", "BAŞ",
			"YAĞLI", "SELÇUK", "GEZİCİ", "KURT", "BADEM", "BERKAY", "ÖZTÜRK", "DÜNDAR", "AKAN", "SAKALLI", "GÜL",
			"TEVDİK", "BULUT", "SARIÇİÇEK", "DURMAN", "GÖRMÜŞ", "HAŞİMOĞLU", "KILINÇ", "POLAT", "ARSLAN", "KALYENCİ",
			"TURANOĞLU", "ORHAN", "YILMAZ", "KUŞ", "SERİN", "KESER", "ŞAHİN", "YÜZER", "ÖZCANOĞLU", "DERE", "YETİZ",
			"ADISANLI", "AKEL", "TAŞKIN", "MART", "AYDIN", "AKIN", "ÖNER", "UÇAR", "KORKMAZOĞLU", "NİMETİGİL", "ARSLAN",
			"ÇINAR", "TATLI", "ATCI", "ÖZER", "ULUTAŞ", "KARATAŞ", "ÇİFTÇİ", "ÖZÜDOĞRU", "ÖKMEN", "KOPTUR", "DEDEMOĞLU",
			"KAYA", "ÖZBEYLER", "BAĞLI", "DURSUN", "BEKAR", "KOÇ", "ÖZDEMİR", "MENDEŞ", "GÜNGÖR UZUNOĞLU", "YAVUZ",
			"HİÇYILMAZ", "ARAS", "TEKİN", "YEŞİLFİDAN", "SARAÇ", "GÜVEREN", "KIVRAK", "ERDAĞ", "SEVEN", "GÜL", "YILDIZ",
			"AYDEMİR", "KAYA", "SAATÇIOĞLU", "GENÇTÜRK", "YALÇIN", "AKSU", "CEYLAN", "AYYILDIZ", "YILDIRIM", "DAĞLI",
			"MÖNÜR", "KESİK", "COŞKUN", "AZAR", "BARAN", "ARTUÇ", "YILMAZ", "TIRAŞ", "GÜNEŞ", "BAKAR", "DİNÇER",
			"ALICI", "ŞİMŞEK", "ÜNAL", "YILMAZ", "DORUK", "DAĞLIOĞLU", "KAAN", "MAVİ", "TEZEL", "GENEL", "KÖYLÜOĞLU",
			"ERDEN", "SEÇKİN", "KAYLAN", "ATİK", "ASLANER", "EVCİ", "YÜCEL", "AYTAÇ", "MUTLU", "COŞGUN", "SERTEL",
			"COŞKUN", "BURAK", "ERDEM", "BAGLARS", "PARA", "CAF", "SEVİMLİ", "CANTAY", "KOLSUZ", "ŞAHİN", "İMALI",
			"GEÇMEN", "ŞENÖZ", "GÜL", "ÇELEBİ", "ATAR", "ÇALIK", "KANAT", "GÖKTÜRK", "BAŞTEMİR", "ALMOFTI", "GEDÜK",
			"KÖSE", "İLHAN", "TOPCUOĞLU", "PEKER", "YAMAK", "ÖZTÜRK", "ÇOLAKEROL", "AVLANIR", "KEÇECİ", "KOÇOĞLU",
			"AKMAN", "DEDEOĞLU", "TEKİR", "TOKMAK", "ARI", "AKSAÇ", "ARICI", "ÖNAL", "BAŞ", "GÜREL", "AFŞAR",
			"BÖLÜKBAŞ", "ÇÖL", "ALTUN", "KUDAY", "KARTAL", "EYLEN", "KILBACAK", "MEMİŞ", "GÜLEÇ", "ÇAMUR", "BAL",
			"CANİK", "TOZLU", "BİLGİN", "SANCAR", "UYAR", "İRİS", "ERSOY", "YARDIMCI", "SİVRİ", "ARSLAN", "BİLGE",
			"BÜYÜKCERAN", "ARI", "ORTA", "AKINCI", "AYDIN", "PARASIZ", "YÜCEL", "İNCE", "TAŞCI", "BAYRAMOĞLU",
			"YILANLI", "KAVURGACI", "TEĞİN", "GÜLER", "KARAN", "ÖZ", "KAYKISIZ", "DİVARCI", "TEĞİN", "PIRTI",
			"DEMİRCAN", "YILMAZ", "MİRZA", "KISAOĞLU", "YILMAZ", "YENİAY", "YILMAZ", "AKTAŞ", "KUNAK", "ERDOĞAN",
			"ERDOĞAN", "GÜLDAMLA", "ATAK", "ÇINARLIK", "ERKEN", "SAKALLIOĞLU", "KARASU", "SEMERCİ", "KOCAYİĞİT", "ATİK",
			"DENİZ", "ATÇEKEN", "PAZIR", "AYRANCI", "ÇÖLBE", "TOPUZ", "KALA", "YANNİ", "YILDIZ", "ÖZALP", "ADALI",
			"ALTUNYAPRAK", "KAŞIKÇI", "DEMİRBAŞ", "YILMAZ", "SEKİN", "YÖNET", "KÜÇÜKCERAN", "GÜVEN", "İNAN", "TANİŞ",
			"UĞURLU", "ŞAHAN", "BOLATKALE", "KESKİN", "GEYİK", "ALAYBEYOĞLU", "SAYYİĞİT", "CERAN", "İZCİ", "HÖKE",
			"SAĞLAM", "SEYREK", "ERKAN", "ÇETİN", "ÖZTÜRK", "UZUNCA", "AYDIN", "UZUNCAN", "ÜNAL", "PEKDOĞAN", "KAYA",
			"ÖZDEMİR", "ERSÖZ", "ALKIŞ", "ZENGİN", "YİLDİZ", "ŞENER", "BÜYÜKKALAYCI", "YILMAZ", "DEMİREL", "MORAN",
			"ÇAYLI", "ÇANGAL", "VURAL", "KUBAT", "KATRAN", "LEYMUNÇİÇEĞİ", "AYDIN", "BEKAR", "ÜNAL", "PİRİ", "BOZKUŞ",
			"KINALIOĞLU", "TARAKCI", "ÜRKMEZ", "ELGÖRMÜŞ", "YAĞLI", "ÇELİK", "ÖZKAYA", "YANARDAĞ", "ÖZHAN", "KARAAĞAÇ",
			"SEZGİN", "KAPLANKIRAN", "TİLBE", "BAYRAKTAR", "TOPÇU", "GÖKÇEK", "KÜÇÜK", "GÖKÇEK", "YİĞİT", "MOLLA",
			"KOÇ", "ÇANKAYA", "ÇEVİK", "YILDIZ", "İNCE", "KASAP", "YUMAK", "ÇETİNKAYA", "KARADAŞ", "KUTAN", "ÖZDENOĞLU",
			"YILMAZ", "KONUK", "TURAN", "AKIŞ", "KIZILMEŞE", "ÇANTAY", "CAMCI", "DERHEM", "AHÇI", "ÖZTÜRK", "PEHLİVAN",
			"TEMEL", "KIRAN", "KIZILTOPRAK", "KAYIHAN", "DEMİR", "GÜNYEL", "ÖZANLAĞAN", "ŞERBETÇİ", "SENEMTAŞI", "KÖSE",
			"KARADAĞ", "DEMİR", "GÜÇLÜ", "ÇİÇEK", "TATLI", "NOZOĞLU", "KARTAL", "BAYRAM", "ATEŞ", "YAPRAK", "GENÇ",
			"KOCABAŞ", "MUTLU", "AKYÜZ", "İŞTAR", "KILINÇ", "ERDEM", "ARICI", "OKUDAN", "TUNÇ", "YILDIZ", "BARUT",
			"KILIÇCIOĞLU", "ERTAN", "YAVUZ", "ÇELİK", "YÜKSEL", "GÜZEL", "ÖZDEMİR", "KOÇAR", "TAŞLI", "İSTEK", "TEMLİ",
			"ARSLANKEÇECİOĞLU", "ŞENGÜL", "ÇETİNER", "EROĞLU", "DAĞDELEN", "ÇETE", "DENİZ", "İSEN", "KARAMAHMUTOĞLU",
			"İZGİ", "DUYU", "DEMİR", "BALABAN", "YILDIZ", "İLKER", "ÖZKAN", "GÜLSOY", "DOĞAN", "GEDİKLİ", "AKGÜN",
			"KARAKAYA", "TAŞ", "TOYRAN", "İBİŞOĞLU", "DURAN", "TEKİNSOY", "YAPAR", "TAŞ", "SÖYLEMEZ", "AVCI", "ÜNLÜ",
			"ZENGİN", "MENEKŞE", "ARICI", "ŞAHİN", "IŞIK", "SAVĞA", "AKBAŞ", "İLİK", "YILDIZ", "KELEŞ", "ÖZKAN",
			"DİNCER", "TURGUT", "OĞUZSOY", "AŞAN", "IŞIK", "UÇAR", "AKMAN", "TEKER", "YILDIRIMÇAKAR", "KESER", "ÇELİK",
			"KARAKUŞ", "KISA", "AKBAŞ", "YALINKILIÇ", "ÇİFTÇİ", "AYAZ", "SADUNOĞLU", "GÜLER", "KILINÇ", "URAL",
			"GÜLTEKİN", "KARACA", "KANSU", "YAĞAN", "BÜDÜN", "DEMİR", "YÜCE", "ÖZDEMİR", "YÜKSEL", "ÖTER", "KAHRAMAN",
			"DUMAN", "KOPAN", "KARAASLAN", "KAHREMAN", "AVCI", "KURUL", "BÜYÜK", "SAĞLAM", "SANSARCI", "AKKAN",
			"PİRDOĞAN", "RENÇBER", "DAĞDELEN", "SÜLÜK", "KÖSE", "YILMAZ", "SAĞLAM", "KARAMAN", "MUTLUER", "ERDEM",
			"EYÜP", "YAKIŞAN", "TEKİN", "NALBANT", "YILDIRIM", "ERŞEN", "SARIEKİZ", "ÇETİNKAYA", "ALKAÇ", "ŞEN",
			"GÜLEŞEN", "UYAR", "GÜÇLÜ", "YAZICI", "DOĞANGÜZEL", "AKKAYA", "FEYZULLAHOĞLU", "ŞAHİN"});

}
