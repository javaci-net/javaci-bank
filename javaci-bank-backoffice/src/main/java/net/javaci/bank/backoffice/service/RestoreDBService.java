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

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
			long start = System.currentTimeMillis();
			log.info("DB generation started");
			initDB();
			long end = System.currentTimeMillis();
			log.info("DB generation end in " + (end - start) + " ms");
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
		dummy.setAccountName(currency.name() + "_Account");
		dummy.setDescription(currency.name() + " hesabim");
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
				email = convertToEnglish(toLower(user.getFirstName())) + (withDot ? "." : "")
						+ convertToEnglish(toLower(user.getLastName()));
			} else {
				email = convertToEnglish(toLower(user.getMiddleName())) + (withDot ? "." : "")
						+ convertToEnglish(toLower(user.getLastName()));
			}
		} else {
			// with short name
			if (random.nextBoolean()) {
				// first name short
				if (user.getMiddleName() == null || random.nextBoolean()) {
					email = convertToEnglish(toLower(user.getFirstName().substring(0, 1))) + (withDot ? "." : "")
							+ convertToEnglish(toLower(user.getLastName()));
				} else {
					email = convertToEnglish(toLower(user.getMiddleName().substring(0, 1))) + (withDot ? "." : "")
							+ convertToEnglish(toLower(user.getLastName()));
				}
			} else {
				// last name short
				if (user.getMiddleName() == null || random.nextBoolean()) {
					email = convertToEnglish(toLower(user.getFirstName())) + (withDot ? "." : "")
							+ convertToEnglish(toLower(user.getLastName().substring(0, 1)));
				} else {
					email = convertToEnglish(toLower(user.getMiddleName())) + (withDot ? "." : "")
							+ convertToEnglish(toLower(user.getLastName().substring(0, 1)));
				}
			}
		}

		return email + "@javaci.net";
	}

	private String generatePassword() {
		return new String[] { "java", "javaci", "javacinet", "javacibank" }[random.nextInt(4)];
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
		return name.replaceAll("İ", "I").replaceAll("Ö", "O").replaceAll("Ü", "U").replaceAll("Ğ", "G")
				.replaceAll("Ş", "S").replaceAll("Ç", "C").replaceAll("ö", "o").replaceAll("ü", "u")
				.replaceAll("ğ", "g").replaceAll("ş", "s").replaceAll("ç", "c");
	}

	private static Random random = new Random();

	public static void main(String[] args) {
		for (String n : NAME_LIST) {
			if (n.indexOf(' ') != -1)
				System.out.println(n);
		}

		for (String n : SURNAME_LIST) {
			if (n.indexOf(' ') != -1)
				System.out.println(n);
		}
	}

	private static final List<String> NAME_LIST = Arrays.asList(new String[] { "JALE", "ALİ", "MAHMUT", "MANSUR",
			"KÜRŞAD", "GAMZE", "MİRAÇ", "YÜCEL", "KUBİLAY", "HAYATİ", "BEDRİYE", "MÜGE", "BİRSEN", "SERDAL", "BÜNYAMİN",
			"ÖZGÜR", "FERDİ", "REYHAN", "İLHAN", "GÜLŞAH", "NALAN", "SEMİH", "ERGÜN", "FATİH", "ŞENAY", "SERKAN",
			"EMRE", "BAHATTİN", "IRAZCA", "HATİCE", "BARIŞ", "REZAN", "FATİH", "FUAT", "GÖKHAN", "ORHAN", "MEHMET",
			"EVREN", "OKTAY", "HARUN", "YAVUZ", "PINAR", "MEHMET", "UMUT", "MESUDE", "HÜSEYİN", "CAHİT", "HAŞİM",
			"ONUR", "EYYUP", "SABRİ", "MUSTAFA", "MUSTAFA", "UFUK", "AHMET", "ALİ", "MEDİHA", "HASAN", "KAMİL", "NEBİ",
			"ÖZCAN", "NAGİHAN", "CEREN", "SERKAN", "HASAN", "YUSUF", "KENAN", "ÇETİN", "TARKAN", "MERAL", "LEMAN",
			"ERGÜN", "KENAN", "AHMET", "URAL", "YAHYA", "BENGÜ", "FATİH", "NAZMİ", "DİLEK", "MEHMET", "TUFAN", "AKIN",
			"MEHMET", "TURGAY", "YILMAZ", "GÜLDEHEN", "GÖKMEN", "BÜLENT", "EROL", "BAHRİ", "ÖZEN", "ÖZLEM", "SELMA",
			"TUĞSEM", "TESLİME", "NAZLI", "GÜLÇİN", "İSMAİL", "MURAT", "EBRU", "TÜMAY", "AHMET", "EBRU", "HÜSEYİN",
			"YAVUZ", "BAŞAK", "AYŞEGÜL", "EVRİM", "YASER", "ÜLKÜ", "ÖZHAN", "UFUK", "AKSEL", "FULYA", "BURCU", "TAYLAN",
			"YILMAZ", "ZEYNEP", "BAYRAM", "GÜLAY", "RABİA", "SEVDA", "SERHAT", "ENGİN", "ASLI", "TUBA", "BARIŞ",
			"SEVGİ", "KALENDER", "HALİL", "BİLGE", "FERDA", "EZGİ", "AYSUN", "SEDA", "ÖZLEM", "ÖZDEN", "KORAY", "SENEM",
			"ZEYNEP", "EMEL", "BATURAY", "KANSU", "NURAY", "AYDOĞAN", "ÖZLEM", "DENİZ", "İLKNUR", "TEVFİK", "ÖZGÜN",
			"HASAN", "SERKAN", "KÜRŞAT", "SEYFİ", "ŞEYMA", "ÖZLEM", "ERSAGUN", "DİLBER", "MESUT", "ELİF", "MUHAMMET",
			"FATİH", "ÖZGÜR", "SİNAN", "MEHMET", "ÖZGÜR", "MAHPERİ", "ONUR", "İBRAHİM", "FATİH", "SEVİL", "SÜHEYLA",
			"VOLKAN", "İLKAY", "İLKNUR", "ZÜMRÜT", "ELA", "HALE", "YENER", "SEDEF", "FADIL", "SERPİL", "ZÜLFİYE",
			"SULTAN", "MUAMMER", "HAYRİ", "DERVİŞ", "YAŞAR", "GÖKHAN", "TUBA", "HANIM", "MEHRİ", "MUSTAFA", "FERHAT",
			"SERDAR", "MUSTAFA", "ERSAGUN", "ONAT", "ŞÜKRÜ", "OLCAY", "BAŞAK", "SERDAR", "YILDIZ", "AYDIN", "ALİ",
			"HALUK", "NİHAT", "BERKAY", "İSMAİL", "AYKAN", "SELÇUK", "MEHMET", "NEZİH", "MUSTAFA", "TİMUR", "ERHAN",
			"MUSTAFA", "MUTLU", "MEHMET", "HÜSEYİN", "İSMAİL", "EVREN", "OSMAN", "ERSEGUN", "MEHMET", "ELİF", "SERKAN",
			"MESUT", "MEHMET", "HİLMİ", "ASUDAN", "TUĞÇE", "AHMET", "GÖKHAN", "BAŞAK", "CEYHAN", "MUHAMMET", "TAYYİP",
			"ESİN", "ZEYNEP", "GÖKÇE", "EVRİM", "YASİN", "SALİHA", "DENİZ", "BELGİN", "ÖZLEM", "GONCA", "ESRA",
			"SEÇKİN", "ESRA", "FATİH", "MUSTAFA", "FEVZİYE", "MUSTAFA", "ARİF", "BİRGÜL", "ÖZLEM", "ÖZLEM", "FUNDA",
			"BERFİN", "DEMET", "SONAY", "SERÇİN", "ALMALA", "PINAR", "ÜMİT", "SENEM", "DENİZ", "MÜNEVER", "HATİCE",
			"ÖZLEM", "ÖZLEM", "ALİ", "SEÇKİN", "COŞKUN", "ÖZGE", "ZELİHA", "PINAR", "AYBÜKE", "HASİBE", "GÜRKAN",
			"ZÜHAL", "NAZIM", "ZEYNEP", "OSMAN", "AYLA", "BEYZA", "ELİF", "ERAY", "DİANA", "TUBA", "SEMRA", "VELAT",
			"BELGİN", "EMİNE", "SİBEL", "GÖKMEN", "ALPASLAN", "BENHUR", "ŞİRVAN", "DİLEK", "HANDE", "ŞAHABETTİN",
			"MİRAY", "ZERRİN", "İLKNUR", "ELİF", "MÜMTAZ", "TUĞBA", "DİLEK", "MEHMET", "BURHAN", "FUAT", "NİHAL",
			"AYŞEGÜL", "SEMA", "ZAFER", "NURSEL", "GÜLPERİ", "BİLGE", "FATİH", "CENGİZ", "SİMGE", "SEMA", "NİLAY",
			"EMİNE", "RİFAT", "CAN", "SİNAN", "LATİFE", "MEHMET", "NURDAN", "MELTEM", "ÜLKÜHAN", "HASAN", "GÜLDEN",
			"SAMET", "BERNA", "ÖZLEM", "NAFİYE", "KENAN", "SERKAN", "FAZLI", "NURSEL", "ABDULLAH", "ERGÜL", "HASAN",
			"MUSTAFA", "SEBAHAT", "EMİNE", "ERDAL", "LEZİZ", "BİRSEN", "TUBA", "AYŞEN", "EBRU", "TAYFUR", "MELTEM",
			"SERHAT", "AYCAN", "ÖZDEN", "ELİF", "SEVGÜL", "SELDA", "IŞIL", "SİBEL", "JÜLİDE", "ZEHRA", "BERİL", "GÜLÜŞ",
			"İNCİ", "ENGİN", "GÜLBAHAR", "MÜBECCEL", "NURDAN", "HANDE", "ÖZNUR", "HANDAN", "OSMAN", "TURGUT", "EMİN",
			"TONYUKUK", "NEJDET", "MUSTAFA", "GÜLİZ", "İPEK", "NİHAL", "MELDA", "DERYA", "DEMET", "MAHMUT", "EMEL",
			"ÖZNUR", "SONGÜL", "RESA", "GAMZE", "ÜMİT", "DENİZ", "MUAMMER", "MÜSLİM", "ÖMER", "FARUK", "TUĞÇE", "VELİ",
			"ENES", "ZAHİDE", "NURETTİN", "İREM", "SEDAT", "REMZİYE", "SİBEL", "İLKNUR", "YASEMİN", "AYLİN", "EMEL",
			"EMEL", "CENNET", "ŞAFAK", "METİN", "SÜLEYMAN", "MUKADDES", "BARIŞ", "MEHMET", "ALİ", "TEVFİK", "SERDAR",
			"EMİNE", "MÜRŞİT", "MUTLU", "FEZA", "İBRAHİM", "TAYFUN", "SERKAN", "AHMET", "SERKAN", "FATMA", "BERKER",
			"SERDAR", "KUBİLAY", "ERKAN", "KERİM", "İLKNUR", "SERKAN", "MUSTAFA", "RUKİYE", "GÖKTEN", "SEZGİ", "TUĞBA",
			"MURAT", "HATİCE", "HATİCE", "EYLÜL", "AYŞE", "GÜL", "NEVİN", "HABİBE", "KEZBAN", "AYSEL", "TALHA", "DUYGU",
			"GÖZDE", "FIRAT", "EBRU", "GÜLEN", "ECE", "SİBEL", "FULYA", "VEDAT", "HARUN", "FİLİZ", "NURAY", "ŞİRİN",
			"ÖZLEM", "BURCU", "PINAR", "HATUN", "CEYDA", "BURCU", "AYŞE", "ALPER", "FEYZA", "HACI", "MURAT", "MÜCELLA",
			"FEYZAHAN", "ŞENAY", "MERİH", "YUSUF", "ARDA", "EVRE", "KONURALP", "KIVANÇ", "EMİNE", "VOLKAN", "NİHAT",
			"RENGİN", "ASLIHAN", "EMRE", "ARİFE", "ESRA", "SEDAT", "MURAT", "CEM", "ERHAN", "ÖMÜR", "UMUT", "CAN",
			"MUSTAFA", "NAFİZ", "DAMLA", "MÜSLİM", "ABDULKADİR", "SAADET", "REZZAN", "SEDAT", "İBRAHİM", "LEYLA",
			"TÜLAY", "ENDER", "YELİZ", "ÖZGÜL", "HALE", "BERÇEM", "MUSTAFA", "TUBA", "SABAHATTİN", "ŞAFAK", "EVRİM",
			"REŞAT", "MUMUN", "FUNDA", "ÖZLEM", "NUR", "METE", "TÜLAY", "ÖZLEM", "HATİCE", "NİLDEN", "MELTEM", "EDA",
			"MELTEM", "MUSTAFA", "YURDUN", "SEMA", "TUBA", "SERPİL", "CENK", "TANER", "ZEKERİYA", "MUHAMMED", "ALİ",
			"TUĞRUL", "YÜCEL", "ESMA", "ÖZLEM", "AHMET", "SEVDE", "NUR", "SAMİ", "GAMZE", "GÜLSÜM", "SERHAT", "BARIŞ",
			"GÜLSEREN", "SULTAN", "İLKER", "DERAM", "AHMET", "BALA", "BAŞAK", "FEVZİ", "FIRAT", "GÖZDE", "FERHAN",
			"İLKER", "SALİM", "EMİNE", "MURAT", "ATAKAN", "REFİK", "MUSTAFA", "ÖZGÜR", "İKLİL", "ZÜHAL", "GÜLSÜM",
			"MEHTAP", "DENİZ", "ÜMİT", "MEHMET", "VOLKAN", "İLKNUR", "SELÇUK", "ÖZLEM", "GÖKHAN", "METE", "HÜMEYRA",
			"PAPATYA", "LEVENT", "FADİME", "SEVGİ", "ERSEN", "ŞULE", "MİNE", "MELİA", "ŞERMİN", "AYLİA", "AHMET",
			"EMRE", "VEDAT", "HALUK", "SEZGİN", "ZEHRA", "BETÜL", "VOLKAN", "ÜNSAL", "KORAY", "GÜLŞAH", "HİCRAN",
			"YUSUF", "KENAN", "YUSUF", "ORHAN", "FÜSUN", "ÖZLEM", "MEHMET", "SERKAN", "İKRAM", "ÜLKÜHAN", "NUH",
			"İSMAİL", "GÜLŞAH", "AYKUT", "NEŞE", "NEZAKET", "MUHAMMET", "DEVRAN", "HANDAN", "ATİLLA", "AYŞEGÜL",
			"PINAR", "ARZU", "NEŞE", "CEYHAN", "HASAN", "SAMİ", "MEHMET", "SALİH", "FERDA", "DİLEK", "AYHAN", "HASAN",
			"ULAŞ", "SUNA", "SELAMİ", "SÜREYYA", "BURCU", "CEM", "YAŞAR", "ÇAĞDAŞ", "DOĞAN", "AHMET", "HATİCE", "HAYRİ",
			"GÜHER", "SUNA", "ARZU", "AHMET", "AHMET", "SEMİH", "YUSUF", "ALİ", "ELİF", "ÇİLER", "ELİF", "YETKİN",
			"BURAK", "VEYSEL", "BURCU", "REFAETTİN", "AYŞE", "GÜL", "HÜSEYİN", "KUNTER", "EMİNE", "EBRU", "ESRA", "NUR",
			"SAMİ", "MUSTAFA", "GÜRHAN", "ORHAN", "HAÇÇE", "MEHMET", "MURAT", "NAZAN", "TUĞBA", "OĞUZHAN", "BERFİN",
			"CAN", "ÖZGÜR", "CİHAN", "DERMAN", "NİHAL", "IŞIN", "HATİCE", "DİDEM", "SUAT", "SİMENDER", "ADNAN", "SEZİN",
			"ŞERAFETTİN", "BERRİN", "ÖZGÜR", "TAYFUR", "SERHAT", "FUNDA", "NESLİHAN", "SERVET", "EMRE", "ORÇUN",
			"FATMA", "ESİN", "DERYA", "DUYGU", "HÜSEYİN", "AYKUT", "AYŞE", "SEHER", "ÖZLEM", "SEDA", "ESRA", "CAN",
			"EVREN", "NİLÜFER", "MURAT", "YUSUF", "MUSTAFA", "BARAN", "GÜNEŞ", "FATİH", "MEHMET", "ORHAN", "PINAR",
			"ERHAN", "GÜLDEN", "LATİFE", "SİBEL", "FATMA", "SELCEN", "HALİS", "YELDA", "ZEHRA", "MEHMET", "REŞİT",
			"EMCED", "OKAN", "ABDULLAH", "ARİF", "ÖZLEM", "AYDEMİR", "FATİH", "AYDIN", "BENGÜHAN", "AHMET", "TOLUNAY",
			"TUĞRA", "ÖZGÜR", "YUSUF", "ÖNDER", "TURGUT", "BARAN", "SEYHAN", "ZEKİ", "KADİR", "MURAT", "İHSAN", "DEMİR",
			"MUHAMMET", "MURAT", "MUHAMMED", "BAHADIR", "İLHAN", "YILDIRIM", "OĞUZ", "KAAN", "EFTAL", "MURAT", "GÖKAY",
			"FATİH", "RIFAT", "NURAN", "HABİL", "ÖMER", "ÖZKAN", "SELMA", "NURETTİN", "AHMET", "UTKU", "SÜLEYMAN",
			"CAN", "ONUR", "KADİR", "FİLİZ", "CANSU", "SELCAN", "ABDULLAH", "NESLİHAN", "SEDA", "ELÇİM", "KÜBRA",
			"HÜSEYİN", "BELMA", "MÜCAHİT", "CEYHUN", "GÜLTEKİN  GÜNHAN", "HANDE", "GÜLHANIM", "ÖMER", "ZİYA", "ÇAĞRI",
			"SERKAN", "LEVENT", "İSA", "CEM", "HÜSEYİN", "CEMİLE", "ÇİĞDEM", "MAHMUT", "ÖNDER", "RAŞAN", "İSMAİL",
			"YAVUZ", "ÖMER", "KENAN", "SELÇUK", "EMRE", "SERDAR", "SONER", "ENVER", "MUHLİS", "TİMUR", "LEMAN", "ELA",
			"CEMİL", "BURCU", "SALİHA", "SANEM", "ZELİHA", "ALEVTİNA", "ARZU", "MEHMET", "BİREYLÜL", "AYSEL", "ÖZGÜL",
			"SIDIKA", "TUNA", "ÖVGÜ", "ANIL", "EMİŞ", "NİLGÜN", "ELİF", "SEBİHA", "YUSUF", "ALPER", "IŞIL", "AYŞEGÜL",
			"TÜMAY", "ZERİN", "MEHMET", "FATMA", "ECE", "AHMET", "NEVROZ", "SEMA", "GAMZE", "PINAR", "FATMA", "MELTEM",
			"HALE", "SELMA", "İBRAHİM", "ASLIHAN", "FİLİZ", "BİLGİ", "TUĞBA", "HÜSEYİN", "EVREN", "FERDİ", "BURÇİN",
			"BARIŞ", "BAVER", "MAHMUT", "EMRAH", "KEMAL", "MURAT", "MEHMET", "ÖZER", "GÖKAY", "NECİP", "ERKAN", "ÜMİT",
			"GANİM", "BARAN", "FATİH", "SANCAR", "MUSTAFA", "KEMAL", "DURAN", "HASAN", "GÖRKEM", "ALİ", "OZAN", "VELİ",
			"ÇAĞLAR", "İRFAN", "SEYFİ", "CEM", "CÜNEYT", "ALİ", "FIRAT", "MUHAMMED", "TAHA", "BURAK", "MUSTAFA",
			"NİZAMETTİN", "RECEP", "GANİ", "ARZU", "MAHMUT", "NURİ", "GÜNAY", "BESTE", "CEM", "EMRAH", "HALİL",
			"İBRAHİM", "ESEN", "İBRAHİM", "MELİKE", "MÜRSEL", "KORAY", "UMUT", "SİNAN", "İBRAHİM", "BARIŞ", "BURHAN",
			"MUSTAFA", "KÜRŞAT", "SERDAR", "BORA", "FUAT", "ELİF", "SİBEL", "ADEM", "CEM", "İNAN", "GÖKTEKİN", "DİLARA",
			"SEDA", "CUMHUR", "HALUK", "PINAR", "AYKUT", "ÖYKÜ", "BAŞAK", "ÖMER", "SERHAN", "SULTAN", "MİNE", "CANSU",
			"SUAT", "ÇİĞDEM", "HANDE", "UMUT", "SEDA", "EVRİM", "DİCLE", "İREM", "FATMA", "İLKNUR", "CEMİLE", "AYŞE",
			"FEYZA", "MUAMMER", "EBRU", "DİNÇER", "AYDIN", "AYŞE", "AHSEN", "PINAR", "SÜREYYA", "BURCU", "MARİA",
			"SEÇİL", "BARIŞ", "ŞAHİNDE", "MEHMET", "NEVRİYE", "NİLAY", "RÜŞTÜ", "ASLI", "TANSU", "ASLIHAN", "NURCAN",
			"DEMET", "ERDEM", "BETÜL", "EMİNE", "ÇETİN", "PINAR", "RASİM", "AHMET", "ZEHRA", "SELİM", "DENİZ", "MESUT",
			"AYSEL", "MÜMÜNE", "DUÇEM", "YAKUP", "ERCAN", "MEHMET", "GÖKÇE", "ATİLLA", "SÜLEYMAN", "MERAL", "NURDAN",
			"SEÇİL", "ELİF", "HASAN", "BİLEN", "NİLAY", "HİLAL", "MUSTAFA", "ŞEYMA", "MELİHA", "MÜJDAT", "BURCU",
			"ARİF", "AYŞE", "HACİ", "HALİL", "IŞIK", "SERAY", "SERHAT", "BURKAY", "EMİN", "ÇAVLAN", "ŞEREF", "CAN",
			"KADİR", "MÜBERRA", "AYŞE", "HASAN", "SAVAŞ", "AYŞE", "NUR", "SÜLEYMAN", "DUYGU", "HACI", "MEHMET",
			"MEHTAP", "ERKAN", "SEMİNE", "ELİF", "RAMAZAN", "AHMET", "EMRE", "SERKAN", "DENİZ", "MİNE", "FATİH", "İREM",
			"ENDER", "YASEMİN", "SEMA", "MUSTAFA", "ULAŞ", "ALİ", "EMİNE", "TÜLAY", "BİRSEN", "GÜLSEN", "TUBA",
			"HAYRİYE", "BAHADIR", "SEZEN", "EDİP", "GÜVENÇ", "MEHTAP", "FİLİZ", "EYLEM", "RAMAZAN", "BİLGİN", "ESRA",
			"ATİYE", "MELTEM", "METİN", "ÖZLEM", "OSMAN", "AYŞE", "HİLAL", "YAVUZ", "ÖZLEM", "AYŞE", "MUSTAFA",
			"ASUMAN", "ÖMER", "ŞENAY", "GÜLNAME", "ÖZLEM", "BETÜL", "EMİNE", "DİLEK", "ZELİHA", "ESİN", "SEREN",
			"FİLİZ", "ULAŞ", "HALE", "ADEM", "İLKER", "ERHAN", "FARUK", "İBRAHİM", "SERKAN", "MAHMUT", "ESAT", "ERAY",
			"OKTAY", "DENİZ", "OSMAN", "İZZET", "İHSAN", "URAL", "ARİF", "AZİZ", "FUAT", "ERNİS", "HAKAN", "MUZAFFER",
			"OĞUZ", "MAHİR", "TAYYAR", "ALP", "EREM", "CİHAN", "MEHMET", "ÜMİT", "MEHMET", "YASEMİN", "CEYDA", "FATIMA",
			"İLAY", "NİLAY", "HURİYE", "MERVE", "ŞEYMA", "SELAHATTİN", "İLKER", "ÖMER", "SÜLEYMAN", "TAYFUN", "ESER",
			"MEHMET", "CİHAT", "İSMAİL", "ABDULLAH", "MURAT", "MUSTAFA", "AYDOĞAN", "HAMDİYE", "KERİME", "RUMEYSA",
			"MELAHAT", "UYSAN", "EMRAH", "HAYRİ", "ÜSTÜN", "MURAT", "HÜSEYİN", "SAİD", "SELİM", "ZEKERİYA", "FATİH",
			"DERYA", "NEVAL", "SEZER", "ÖZGE", "HATİKE", "ÖZGÜR", "ŞAFAK", "AHMET", "TUNÇ", "ÖZDEN", "OKAN", "RAZİYE",
			"DAMLA", "MAHMUT", "LALE", "EYÜP", "DERYA", "SELAHATTİN", "ZEKİ", "HÜSEYİN", "MELEK", "BİNNUR", "ONUR",
			"SALİH", "ERKİN", "MURAT", "ERHAN", "BURAK", "AYŞE", "CEREN", "NUMAN", "ÖZLEM", "ÜZEYİR", "CAN", "MUSTAFA",
			"KORHAN", "ALPER", "FUNDA", "HAMZA", "MUSTAFA", "RAŞİD", "YUSUF", "MELİKE", "ŞÜKRÜ", "KASIM", "GÖKSEL",
			"HİKMET", "ASIM", "ÜMRAN", "TEKİN", "MURAT", "HACER", "BERAY", "HANIM", "EDA", "NURAY", "ÖMER", "FARUK",
			"FERHAT", "OĞUZ", "BENGÜ", "AHMET", "MERT", "PINAR", "EMRE", "KÜBRA", "SELÇUK", "ŞENOL", "ORHAN", "MÜMÜN",
			"FATİH", "MURAT", "MERT", "KADİR", "KAAN", "SUAT", "ERCAN", "GÖKHAN", "DENİZ", "CİHAN", "CEM", "ALİŞAN",
			"ALİ", "SAİP", "ABİDE", "MERVE", "YUSUF", "ÖMER", "SERDAR", "YÜKSEL", "AHMET", "MAHMUT", "BURAK", "YASEMİN",
			"İBRAHİM", "FUAT", "MUSTAFA", "BİLGE", "MERVE", "AHMET", "CEVDET", "SEHER", "MEHMET", "DENİZ", "SAMET",
			"SAMET", "SANCAR", "ŞULE", "SELİM", "KADİR", "CAHİT", "HAYRUNNİSA", "UĞUR", "RESUL", "MUSTAFA", "BUĞRA",
			"İSHAK", "ESRA", "SANEM", "GÖKÇEN", "MERVE", "EZGİ", "MUSTAFA", "GÜVENÇ", "OĞUZ", "KAĞAN", "ESMA", "ÖMER",
			"AYKUT", "MEHMET", "ŞİRİN", "OSMAN", "AHMET", "NAİL", "İREM", "ELA", "BAHRİ", "ALİ", "RIZA", "ADEM",
			"MEHMET", "BAYRAM", "FURKAN", "HALİL", "KÜBRA", "SEFA", "MEHTAP", "FATİH", "BERKAN", "HÜSEYİN", "VOLKAN",
			"ŞEBNEM", "TUBA", "AYHAN", "ENNUR", "ALİ", "RIZA", "FATİH", "GÖZDE", "SEVİL", "EDA", "HATİCE", "ZARİFE",
			"BURAK", "İREM", "KÜBRA", "EMİNE", "DİLEK", "TUBA", "HALİL", "ESER", "EBUBEKİR", "ONUR", "MEHMET", "VEYSEL",
			"BURCU", "AYŞEGÜL", "DERYA", "SEMA", "ÇAĞLA", "KADİR", "BURAK", "MELİKE", "BAYRAM", "AYŞEGÜL", "TUĞBA",
			"MEHMET", "ELİF", "TÜRKER", "SEMİH", "HATİCE", "CANSU", "SERHAT", "TANER", "İDRİS", "SEVİNÇ", "FERİDE",
			"EVREN", "TUĞBA", "MUHAMMED", "MUCAHİD", "HİLAL", "DİLEK", "NAZLI", "ŞİFA", "GÜLÇİN", "KEZBAN", "YELDA",
			"SEDA", "CEYLAN", "BURAK", "UĞUR", "ŞEYMA", "SELMA", "EMRE", "MUHAMMED", "FATİH", "AHMET", "HASAN",
			"RUKİYE", "ADİL", "MUSTAFA", "MUSTAFA", "ASIM", "METİN", "CEREN", "SÜLEYMAN", "MELİKE", "TUĞBA", "ÇAĞRI",
			"BURHANETTİN", "İBRAHİM", "YUNUS", "EMRE", "KADİR", "MEHMET", "İLKER", "GÖKHAN", "KEMAL", "KÜRŞAT", "AHMET",
			"ZEHRA", "AZİZ", "İSA", "GÖKHAN", "BARIŞ", "MEHMET", "SUPHİ", "ASİYE", "BURCU", "ZEYNEP", "YÜKSEL", "UĞUR",
			"VOLKAN", "UFUK", "TUĞBA", "SELİM", "SELÇUK", "SALİHA", "DİLEK", "ÖZKAN", "ÖVÜNÇ", "OSMAN", "NUR", "ALEYNA",
			"MUHAMMED", "FURKAN", "MEHMET", "VEHBİ", "MEHMET", "İKBAL", "MEHMET", "LEYLA", "İZZETTİN", "İSMAİL",
			"İSMAİL", "İHSAN", "BURAK", "İBRAHİM", "HİLAL", "HAYRİYE", "GİZEM", "ESİN", "ERCAN", "ENES", "TAHİR",
			"DİDEM", "DENİZ", "BUĞRA", "BİLGİN", "ALİ", "MUHARREM", "ALİ", "ADEM", "ADEM", "IŞIL", "YUSUF", "MUSTAFA",
			"HÜSEYİN", "EZEL", "EMİNE", "ELİF", "CEREN", "AYŞE", "GİZEM", "DEMET", "DİDEM", "AYŞE", "ESER", "AYŞE",
			"GÜLÇİN", "HACER", "FERAT", "ZEYNEP", "KIYMET", "ASLAN", "ALİ", "GİZEM", "CUMA", "MEHTAP", "HAMDİ", "ORHAN",
			"NURİ", "ANIL", "SEMRA", "ÖKKEŞ", "VEYSEL", "ŞERİFE", "YASEMİN", "MEHMET", "MEHMET", "TOLGA", "OĞUZHAN",
			"MUSTAFA", "MAHSUM", "İSMAİL", "MEHMET", "PERVİN", "MURAT", "SALİM", "YUNUS", "VEYSEL", "ÖMER", "AHMET",
			"AYDIN", "ABDULKADİR", "AHMET", "AYŞEGÜL", "AZİZ", "ABDULLAH", "ABDULLAH", "YILDIRIM", "AYŞEGÜL", "İBRAHİM",
			"SİBEL", "MUZAFFER", "MUHAMMED", "YUSUF", "HAMZA", "HAKAN", "NESRİN", "EMRAH", "KEZİBAN", "MEHMET", "ALİ",
			"MELİKE", "ELİF", "BURHAN", "CEVAHİR", "CİHAN", "EMRAH", "MEHMET", "ALİ", "FATİH", "MAHMUT", "FATİH",
			"DAVUT", "EDA", "ÖMER", "FARUK", "PELİN", "SALİH", "YUSUF", "ZİYA", "ZERİN", "ÖZKAN", "YUSUF", "UMUT",
			"EMRAH", "ŞEYHMUS", "MERT", "METİN", "MUKADDES", "HAZAN", "HALİM", "MEHMET", "AKİF", "AYCAN", "CEMİL",
			"BERNA", "AHMET", "HÜSEYİN", "ERKAN", "HAKAN", "BAYRAM", "RUMEYSA", "ÇİLE", "VEYSİ", "ZUHAL", "YÜCEL",
			"ESMERALDA", "NİLÜFER", "BURÇHAN", "VOLKAN", "SELCEN", "MUSTAFA", "HATİCE", "BURCU", "AHMET", "BURAK",
			"BÜLENT", "MÜCADİYE", "SONGÜL", "IŞIK", "MELİKE", "MEHMET", "RAŞİT", "AYHAN", "SERDAR", "ERKAN", "EMRAH",
			"ERDAL", "RAHİME", "GÜLDEN", "SİNEM", "İHSAN", "ABDUL", "AZİM", "ÖZTAN", "ŞENOL", "MEHMET", "HAZBİN",
			"İSMAİL", "ŞENOL", "BORA", "HAMZA", "SEDA", "FATİH", "UĞUR", "KEMAL", "ADİL", "MAZLUM", "CEM", "ATAKAN",
			"VOLKAN ", "MUHAMMED ", "ERDEM", "FATMA", "HATİCE", "MEHMET", "EVREN", "ATACAN", "FATMA", "ŞADİ", "GÜLCAN",
			"MERYEM", "KAMERCAN", "ESRA", "ŞERİFE", "SEÇİL", "ZEHRA", "VAZİR", "AKBER", "ZEYNEP", "ALİ", "SAİD",
			"MURAT", "UTKU", "SAİD", "İRFAN", "HALİME", "ÜMMÜ", "GÜLSÜM", "ŞAFAK", "BETÜL", "BAŞAK", "ZEYNEP", "EZGİ",
			"VASFİYE", "ERDEM", "İBRAHİM", "AYŞE", "MEHMET", "VEHBİ", "FATMA", "EMEL", "ADEM", "ESEN", "HACI", "KEMAL",
			"EMRE", "ÖZGÜR", "OSMAN", "ÇETİN", "SONGÜL", "MUSTAFA", "ÖZLEM", "ZAFER", "FATİH", "MAKBULE", "SEDA",
			"FATMA", "BEGÜM", "TUĞBA", "OSMAN", "SİNEM", "ANIL", "KEMAL", "EREN", "BİLAL", "BARIŞ", "İBRAHİM",
			"ATILGAN", "BERNA", "SERAP", "BEKİR", "EYÜP", "EGEMEN", "ALİ", "YÜCEL", "TANER", "VAHDETTİN", "TALHA",
			"TOLGAHAN", "MUKADDER", "ENGİN", "SERTAÇ", "CİHAN", "PINAR", "AYŞE", "UĞURAY", "MUSTAFA", "EDA", "ELİF",
			"TAYLAN", "UĞUR", "ABİDİN", "BULUT", "CUNDULLAH", "ADNAN", "ERSİN", "ERHAN", "HÜSEYİN", "OĞUZ", "HAYRİ",
			"NURİ", "SELMA", "ESİN", "YAKUP", "İLKER", "AHMET", "ABDULLATİF", "FATİH", "ÖZLEM", "İNANÇ", "HALİL",
			"İBRAHİM", "ÖZGE", "HASAN", "PINAR", "AYŞE", "AYŞE", "HALİL", "CAN", "CEM", "ÖZGÜR", "SEDA", "UMUT",
			"VOLKAN", "ONUR", "EMRAH", "NAGİHAN", "HÜSEYİN", "MEHMET", "DİRİM", "MİNE", "GİZEM", "ÖMER", "TUĞBA",
			"MERVE", "YÜCE", "AYŞE", "PINAR", "AHMET", "CAN", "MERVE", "EMRE", "ERDİNÇ", "NİLAY", "BUŞRA", "ZÜHAL",
			"CAFER", "İLKER", "TURAN", "RECEP", "EBRU", "ÖMER", "FARUK", "MEHMET", "NURİ", "HİLAL", "NURHAN", "YASEMİN",
			"NAGİHAN", "ALİ", "KEMAL", "PINAR", "GİZEM", "UYGAR", "SEFA", "NURULLAH", "GİZEM", "SEVAL", "ŞERİF",
			"BERFU", "SEMİH", "SIDDIKA", "TUĞBA", "İRFAN", "YILDIRIM", "FAHRİ", "CANSU", "PETEK", "ELİF", "CEM",
			"EMRAH", "BÜŞRA", "NAFİZ", "ŞEYMA", "EKREM", "YASEMİN", "AYŞE", "ÇAĞRI", "MEHMET", "ÖMER", "GÖKHAN",
			"REYHAN", "HÜSEYİN", "ÖZGE", "DERYA", "MUSTAFA", "ESMA", "ÖZLEM", "GÖZDE", "SELİM", "EMİNE", "ÇİĞDEM",
			"ŞERİFE", "EZGİ", "NEFİSE", "TAYFUN", "PELİN", "ÖZGE", "KADER", "BELGİN", "ABDULAZİZ", "EMEL", "NUR",
			"BURCU", "CEYDA", "VEHBİ", "RAHİME", "ALİ", "DENİZ", "MUHAMMED", "EMRE", "HASAN", "ÜBEYDULLAH", "EYÜP",
			"GÖKHAN", "KÜBRA", "İTİBAR", "MUSTAFA", "ABDULLAH", "TUBA", "NURDAN", "HATİCE", "BÜŞRA", "ZEHRA", "CANAN",
			"OSMAN", "BİLGE", "TUBA", "FARİS", "AYHAN", "TURĞUT", "ONUR", "MERVE", "HİDAYET", "TUBA", "ABDULKERİM",
			"MUHAMMED", "RIZA", "DİLEK", "ARDA", "NERMİN", "FAİK", "HALİL", "HASAN", "HÜSEYİN", "SEMA", "MUSA",
			"AYŞEGÜL", "BÜŞRA", "ZEYNEP", "METİN", "CİHAN", "MERAL", "HİLAL", "DİDEM", "İSMAİL", "HASAN", "CIVAN",
			"MEHMET", "AKİF", "FATİH", "CEM", "YUSUF", "HÜSEYİN", "TUBA", "TAYFUN", "MUSTAFA", "HASAN", "RUKİYE",
			"ÖZDEN", "MEHMET", "ESRA", "HASAN", "MUSTAFA", "CİHAD", "EYÜP", "MURAT", "İBRAHİM", "HALİL", "SEDAT",
			"MEHMET", "HÜSEYİN", "MURAT", "HACI", "AYŞE", "FULYA", "FAİK", "RAMAZAN", "MEHMET", "SAVAŞ", "AYHAN",
			"NAİME", "SILA", "RIFAT", "MURAT", "FİKRİ", "HASAN", "YURDAGÜL", "MUHAMMET", "MUSTAFA", "ERCAN", "TUĞBA",
			"MEHTAP", "HAMİT", "FERİT", "DENİZ", "MUSTAFA", "ŞAHİKA", "SERDAR", "TAHSİN", "BATUHAN", "ONUR", "MURAT",
			"EMİNE", "AYÇA", "İLYAS", "ECE", "MUSTAFA", "MURAT", "AHMET", "ÇAĞRI", "NİLGÜN", "MUSTAFA", "ERHAN",
			"VEYSEL", "HAKAN", "FATİH", "BARIŞ", "ANDAÇ", "ŞENOL", "HASAN", "KEMAL", "CEYHUN", "ABDÜLSAMET", "İBRAHİM",
			"AHMET", "SERPİL", "ÜMMÜGÜLSÜM", "YAVUZ", "SÜHEYLA", "AYÇA", "ÖZGE", "TUBA", "MERT", "ÖZGÜR", "MELİHA",
			"ESRA", "BETÜL", "ABDULLAH", "SİNAN", "GÜNDEM", "KEZİBAN", "DERYA", "OĞUZ", "BEDRİ", "MEHMET", "EMRE",
			"ÜMİT", "HALİL", "İBRAHİM", "ÖZGE", "HAZEL", "FATMA", "BETÜL", "MUSTAFA", "AHMET", "BİLGEHAN", "HİKMET",
			"EKİN", "EMİN", "YİĞİT", "ABDULSAMET", "PINAR", "MUSACİDE", "ZEHRA", "BURAK", "TUĞÇE", "MAHMUT", "BAKIR",
			"ELİF", "TUĞÇE", "EKİN", "ASLIHAN", "ESRA", "İSMAİL", "MİKDAT", "UFUK", "MEHMET", "NURİ", "BİLGE", "ELİF",
			"AYŞE", "ERKAN", "SABRİ", "İREM", "BÜŞRA", "SULTAN", "MUSTAFA", "ALİCAN", "AYBEGÜM", "NEFİSE", "ZEKERİYA",
			"ERSİN", "AHMETCAN", "ABDURRAHMAN", "FUAT", "ZEYNEP", "FATİH", "ÇAĞDAŞ", "UĞURAY", "SARE", "ONUR", "SERAP",
			"MURAT", "EMİNE", "MUSTAFA", "BAHADIR", "ZEYNEP", "GÖZDE", "KÜBRA", "BURAK", "MEHMET", "MUHAMMED", "HASAN",
			"İSMAİL", "SAYGIN", "GÖKHAN", "ALPER", "ÇAĞLAR", "CEMAL", "HÜSEYİN", "ONUR", "CEMRE", "MELTEM", "CEYHUN",
			"ASLI", "İBRAHİM", "GÖZDEM", "HİLAYDA", "HASRET", "ŞEBNEM", "ONUR", "CAN", "AYŞEGÜL", "EMİNE", "PELİN",
			"ARMAN", "MERVE", "GAMZE", "ALİŞAN", "ERDEM", "ENES", "ORGÜL", "DERYA", "BAŞAK", "HASAN", "KADİR", "ŞADİYE",
			"SELİN", "CEREN", "ELİFCAN", "SEVAL", "GÖKÇEN", "ÖMER", "FARUK", "ALİ", "HASAN", "MUSTAFA", "KÜBRA", "HACI",
			"HASAN", "GÖKHAN", "MERİÇ", "İREM", "MAHMUT", "SAMİ", "MURAT", "SÜLEYMAN", "SERDAR", "NİHAN", "HANİFE",
			"TUĞÇE", "ŞULE", "ERTAN", "MUHAMMED", "ÇAĞDAŞ", "ALİ", "YUSUF", "HATUN", "İBRAHİM", "MEHMET", "YAVUZ",
			"MEHMET", "ERTAN", "SÜMEYRA", "FATMA", "EFSUN", "RAMAZAN", "MEHMET", "ALİ", "BERKAN", "DAVUT", "YAVUZ",
			"SELİM", "İBRAHİM", "OKAN", "MEHMET", "MERT", "EMRAH", "BURAK", "ALAADDİN", "İDRİS", "BUGRA", "KÜBRA",
			"AYŞEGÜL", "MERVE", "NUR", "SUNA", "ELZEM", "ABDURRAHMAN", "BURAK", "MİHRİMAH", "SELCEN", "RABİA", "MERİÇ",
			"DERYA", "SERKAN", "CEYDA", "AYŞENUR", "MEHMET", "AKİF", "TUĞBA", "SERHAT", "BAYRAM", "MEHMED", "UĞUR",
			"DAMLA", "MUSTAFA", "VEYSEL", "AHMET", "SERKAN", "GÖKHAN", "ABDULSELAM", "MEHMET", "YILDIRIM", "HÜSEYİN",
			"BİLAL", "YAKUP", "GÜLLÜ", "SELCEN", "VEHBİ", "DİLAN", "ÖMER", "FARUK", "EMRE", "MEVLÜT", "NEBİL", "SIDIKA",
			"FATMA", "MARUF", "FIRAT", "EBRU", "MAHMUT", "PINAR", "ALİ", "OSMAN", "HİLAL", "NİLGÜN", "BARIŞ", "ERKAN",
			"CİHAN", "ÖMER", "BEGÜM", "SÜLEYMAN", "ÖMER", "ELİF", "MEHMET", "ZİYA", "HAKAN", "AHMET", "SERCAN", "BİŞAR",
			"MUSTAFA", "NUR", "SERKAN", "ELİF", "HALİL", "İBRAHİM", "EMİNE", "FATİH", "MEHMET", "EREN", "AYTAÇ",
			"ENGİN", "YURDAGÜL", "FULYA", "YASEMİN", "ETHEM", "YASİN", "METİN", "ELİF", "FERAT", "YUSUF", "CİHAN",
			"MUHAMMED", "GÜLŞEN", "ABDULMELİK", "MERVE", "ALİ", "YASEMİN", "MUHAMMET", "BAĞBUR", "YUSUF", "ZİYA",
			"ERDAL", "MURAT", "MERVE", "RAMAZAN", "GÜL", "BEGÜM", "ORHAN", "UYGAR", "HASAN", "SEMA", "GÜNEŞ", "BEYZA",
			"FIRAT", "YİĞİT", "MUHSİN", "HAMZA", "SABAHATTİN", "EMİN", "AHMET", "ÇAĞRI", "NAZLI", "MEHMET", "GÖKSEL",
			"BAŞAK", "HASAN", "YAVUZHAN", "GÖKHAN", "MUHAMMED", "SAMİ", "ENGİN", "EBRU", "METANET", "MEHRALİ", "EZGİ",
			"GİZEM", "NESLİHAN", "İLYAS", "SERKAN", "MEHMET", "SELAHATTİN", "MUSTAFA", "MEHMET", "CİHAN", "MURAT",
			"KASIM", "RAHİME", "MERVE", "OZAN", "ÖZKAN", "ZAFER", "SENAN", "BEDREDDİN", "MURAT", "ZEYNALABİDİN",
			"CAHİT", "DENİZ", "MEHMET", "EMRAH", "ESRA", "TUNCAY", "HÜSEYİN", "YİĞİT", "BÜNYAMİN", "PERVER", "ERDEM",
			"ÖMER", "SERHAT", "EMRE", "MERTER", "ABDÜLKADİR", "ENGİN", "NURULLAH", "ABDULLAH", "MEHMET", "HASAN",
			"AHMET", "MELİH", "ÖKKEŞ", "YILMAZ", "BURAK", "TOLGAHAN", "SÜLEYMAN", "SUAT", "ONUR", "MURAT", "VOLKAN",
			"OSMAN", "ALPER", "ERHAN", "AKIN", "ALİ", "ÖMER", "MUSTAFA", "FATMA", "DUYGU", "TÜRKER", "HASAN", "MUSTAFA",
			"TURAN", "ERGÜN", "ÖZLEM", "NİMET", "NİHAT", "SERHAT", "RAHMİ", "TUNA", "NEZİR", "YUSUF", "EMRE", "SERDAR",
			"RUKİYE", "HACI", "ÖMER", "ÇAĞLA", "ÜMİT", "MEHMET", "FATİH", "NAZIM", "HATİCE", "SİDAR", "MEHMET", "ALİ",
			"BURÇ", "YUNUS", "ÇAĞRI", "SİNAN", "DİNÇER", "AYSUN", "MUSTAFA", "İLKER", "PINAR", "MAHİR", "CEBRAİL",
			"RAMAZAN", "SELAHATTİN", "NEVİN", "MEHMET", "NEDİM", "YASİN", "BURHAN", "SEVCAN", "OSMAN", "SALİH",
			"MAHMUT", "EMRE", "TEYFİK", "SERTAÇ", "HALİM", "AYSU", "EMRE", "GİZEM", "ASUMAN", "GÖKÇE", "ZEHRA", "ORKUN",
			"SAVAŞ", "SELİM", "MURAT", "ERHAN", "FATİH", "ÖMER", "FARUK", "ZÜHRE", "AHMET", "ASENA", "EMRE", "İSMAİL",
			"MEHMET", "FURKAN", "HABİP", "İSMAİL", "ENDER", "HAKAN", "NURGÜL", "BURCU", "SELÇUK", "MAHMUT", "BURAK",
			"MURAT", "İLKER", "NİMET", "DİDEM", "NİHAL", "ŞÜKRİYE", "TUĞÇE", "REŞİT", "VOLKAN", "SELİM", "OZAN",
			"HASAN", "ZEYNEP", "SERKAN", "AHMAD", "TAHER", "HÜSEYİN", "ZAFER", "RAMAZAN", "SELKAN", "MURAD", "CANSEN",
			"KAMİL", "YÜCEL", "DERYA", "YASEMİN", "TOLGA", "ABDULKADİR", "ALİ", "ÖZEN", "FATMA", "MUSTAFA", "HAMZA",
			"MURAT", "CÖMERT", "HÜLYA", "GÖZDE", "BERK", "AHMET", "FURKAN", "YUSUF", "EMRAH", "EMRE", "ABDULSEMET",
			"EYLEM", "İSMAİL", "ÇAĞLAR", "YASEMİN", "MELTEM", "TOLGA", "CAN", "EMRE", "MUSTAFA", "EMİR", "MURAT",
			"FUNDA", "ELİF", "NUR", "NİLAY", "AHMET", "ÇAĞRI", "MERVE", "SETENAY", "SAMET", "ERDEM", "CAN", "SERKAN",
			"ÖZNUR", "ÖZNUR", "İSMAİL", "RAMAZAN", "FATİH", "ŞUAYIP", "ALİ", "SULTAN", "EMRAH", "GÜLBERAT", "UFUK",
			"ZEYNEP", "MUSA", "NURAN", "GÜLAY", "SERDAR", "CANAN", "AHMET", "HÜSEYİN", "ALPER", "CÜNEYT", "İLYAS",
			"HALİLİBRAHİM", "CANSU", "EMEL", "SEVAL", "EBRU", "MUSTAFA", "KEMAL", "MEHMET", "AYSU", "DUYGU", "ASLI",
			"NİHAL", "LEYLA", "BURÇİN", "MERYEM", "MAHMUT", "ARDA", "SERPİL", "AHMET", "KÜRŞAD", "EZGİ", "İPEK",
			"HAVVA", "ŞAHİN", "DENİZ", "ARMAĞAN", "GAMZE", "YAŞAR", "İLKAY", "SAADET", "NİLAY", "HÜSEYİN", "KALKAN",
			"TAMER", "HİDİR", "ADEM", "AHMET", "DERYA", "FATİH", "YAVUZ", "SELİM", "BEYZA", "ÜMİT", "AHMET", "FETHİ",
			"HİDAYET", "MEVLÜT", "YAVUZ", "SELİM", "MEHMET", "FATİH", "FETHİYE", "MERVE", "MEHMET", "KORAY", "EMİNE",
			"MELİKE", "MEHTAP", "SEVDA", "EMİN", "EMİR", "KAAN", "MEHMET", "HİLMİ", "HAVVA", "LÜTFİ", "EMİNE", "ELÇİN",
			"MUSTAFA", "GÖZDE", "GİZEM", "TEVHİD", "AYŞE", "GÜL", "FATMA", "TİMUÇİN", "BİROL", "MUAMMER", "DİLŞAH",
			"YAŞAR", "BARBAROS", "ELİF", "NİHAL", "YASEMİN", "ŞAHİN", "ALİ", "EMRAH", "AYŞE", "YAKUP", "ONUR", "ÇAĞRI",
			"BAHADIR", "KURTULUŞ", "KADİR", "MUSTAFA", "YALÇIN", "CEREN", "BUĞLEM", "ESRA", "ÜMİT", "ŞERİFE", "ŞEYDA",
			"ASLI", "NESLİHAN", "ESRA", "ÇAĞRI", "SERDAR", "MEHMET", "ALİ", "KAMURAN", "DERYA", "PINAR", "MERYEM",
			"ŞİRİN", "HARUN", "HARUN", "PINAR", "BARIŞ", "FATİH", "AVNİ", "İBRAHİM", "DAMLA", "MERVE", "ÖKKEŞ", "CELİL",
			"AYŞE", "RECEP", "EMRE", "HALİL", "MURAT", "SERKAN", "ALEV", "MÜŞERREF", "FATİH", "AHMET", "ADNAN", "HİSAR",
			"CAN", "ONUR", "ALP", "MUSTAFA", "ÇİĞDEM", "SERKAN", "ELİF", "HASAN", "AHMET", "BAKİ", "SEDA", "HANDAN",
			"AYLİN", "SEVİL", "NECMİYE", "GÜL", "ÖZLEM", "VOLKAN", "SERDAR", "EBRU", "ERTUNÇ", "ONUR", "EMRE", "PINAR",
			"MUHAMMED", "SONGÜL", "SÜMEYYE", "MEVSİM", "TUĞBERK", "UĞUR", "AHMET", "BURAK", "EMRE", "YİĞİT", "CAN",
			"FATİH", "MEHMET", "METE", "CAN", "ESRA", "FERHAT", "YELİZ", "SEVGÜL", "CÜNEYT", "SEYFULLAH", "HALİL",
			"CANSUN", "FATMA", "ESRA", "SERDAR", "YUNUS", "EMRE", "SEZAİ", "TAHİR", "VEYSİ", "YILDIRAY", "ELİF",
			"SERHAT", "ÖMER", "ÖZGE", "GÜLSÜM", "DENİZ", "SELİN", "GÜL", "BİLGE", "ŞEHMUS", "DİDEM", "MERVE", "EYYÜP",
			"NAZLI", "HİLAL", "HATİCE", "ÖZGE", "ABDULLAH", "MURAT", "HAYATİ", "CAN", "MERVE", "FERAY", "HASAN",
			"GAMZE", "HASAN", "RAMAZAN", "FERHAD", "BETÜL", "NESLİHAN", "MUSTAFA", "AYTAÇ", "SENA", "ZÜHTÜ", "BENER",
			"EMRE", "OSMAN", "BULUT", "ERSİN", "HALİL", "İBRAHİM", "AHMET", "SELÇUK", "NURMUHAMMET", "YASİN", "ÖZGE",
			"ZEKİYE", "SEVAL", "ERDEM", "DİLEK", "ŞULE", "GÖKNUR", "SALİHA", "VELİT", "MUHAMMED", "NURİ", "İBRAHİM",
			"TURGAY", "ENGİN", "HÜSEYİN", "MESUT", "LEYLA", "TUBA", "MEHMET", "ALİ", "SALMAN", "ŞEYHMUS", "ZEHRA",
			"NURDAN", "CEBBAR", "ÖMER", "AHMET", "SİNAN", "ALPEREN", "MEHMET", "SELAMİ", "ABDULAZİZ", "LEYLA", "FATMA",
			"MÜNEVER", "MÜRSELİN", "EMRULLAH", "EDA", "ÜMİT", "BÜNYAMİN", "RIDVAN", "ADEM", "ORHAN", "EMİNE", "KEMAL",
			"FATMA", "ALİ", "MEHMET", "MUSTAFA", "NEVZAT", "OSMAN", "ONUR", "MUSTAFA", "ALİ", "MEHTAP", "MURAT", "MUSA",
			"FİLİZ", "HALENUR", "KEZİBAN", "EFRUZ", "EMİN", "FATİH", "ÜNAL", "VOLKAN", "BAYRAM", "BERAAT", "MEHMET",
			"MÜCAHİT", "SABRİ", "SEFA", "RAŞAN", "RABİA", "ŞEBNEM", "AHMET", "AHMET", "ZÜBEYDE", "BÜŞRA", "BURCU",
			"ALİ", "IŞIL", "GÖZDE", "ESMA", "KÜBRA", "YAŞAR", "GÖZDE", "YILMAZ", "ZEYNEP", "CANER", "VOLKAN", "EMRE",
			"KAĞAN" });

	private static final List<String> SURNAME_LIST = Arrays.asList(new String[] { "MANSUR", "KÜRŞAD", "BEDRİYE", "MÜGE",
			"HÜSEYİN", "CAHİT", "HAŞİM", "ONUR", "EYYUP", "SABRİ", "AHMET", "ALİ", "YUSUF", "KENAN", "MERAL", "LEMAN",
			"TUFAN", "AKIN", "TESLİME", "NAZLI", "HÜSEYİN", "YAVUZ", "BATURAY", "KANSU", "TEVFİK", "ÖZGÜN", "HASAN",
			"SERKAN", "MUHAMMET", "FATİH", "ÖZGÜR", "SİNAN", "MEHMET", "ÖZGÜR", "ZÜMRÜT", "ELA", "MUAMMER", "HAYRİ",
			"YAŞAR", "GÖKHAN", "TUBA", "HANIM", "MUSTAFA", "FERHAT", "MUSTAFA", "ERSAGUN", "OLCAY", "BAŞAK", "ALİ",
			"HALUK", "NİHAT", "BERKAY", "MEHMET", "HÜSEYİN", "İSMAİL", "EVREN", "OSMAN", "ERSEGUN", "MEHMET", "HİLMİ",
			"ASUDAN", "TUĞÇE", "AHMET", "GÖKHAN", "MUHAMMET", "TAYYİP", "ZEYNEP", "GÖKÇE", "MUSTAFA", "ARİF", "ALMALA",
			"PINAR", "ALİ", "SEÇKİN", "BELGİN", "EMİNE", "GÖKMEN", "ALPASLAN", "BENHUR", "ŞİRVAN", "MEHMET", "BURHAN",
			"SEMA", "NİLAY", "RİFAT", "CAN", "SERKAN", "FAZLI", "AYCAN", "ÖZDEN", "JÜLİDE", "ZEHRA", "BERİL", "GÜLÜŞ",
			"OSMAN", "TURGUT", "EMİN", "TONYUKUK", "ÖMER", "FARUK", "VELİ", "ENES", "NURETTİN", "İREM", "EMEL",
			"CENNET", "MEHMET", "ALİ", "İBRAHİM", "TAYFUN", "AHMET", "SERKAN", "AYŞE", "GÜL", "GÜLEN", "ECE", "HACI",
			"MURAT", "RENGİN", "ASLIHAN", "ARİFE", "ESRA", "UMUT", "CAN", "MUSTAFA", "NAFİZ", "FUNDA", "ÖZLEM",
			"HATİCE", "NİLDEN", "MUHAMMED", "ALİ", "ESMA", "ÖZLEM", "SEVDE", "NUR", "BALA", "BAŞAK", "FEVZİ", "FIRAT",
			"ZÜHAL", "GÜLSÜM", "FADİME", "SEVGİ", "ŞULE", "MİNE", "ZEHRA", "BETÜL", "MUHAMMET", "DEVRAN", "HASAN",
			"SAMİ", "HASAN", "ULAŞ", "CEM", "YAŞAR", "ELİF", "ÇİLER", "AYŞE", "GÜL", "ESRA", "NUR", "MUSTAFA", "GÜRHAN",
			"MEHMET", "MURAT", "BERFİN", "CAN", "FATMA", "ESİN", "SEHER", "ÖZLEM", "ESRA", "CAN", "MUSTAFA", "BARAN",
			"FATMA", "SELCEN", "MEHMET", "REŞİT", "ABDULLAH", "ARİF", "ÖNDER", "TURGUT", "MUHAMMET", "MURAT", "OĞUZ",
			"KAAN", "EFTAL", "MURAT", "FATİH", "RIFAT", "ÖMER", "ÖZKAN", "ONUR", "KADİR", "CANSU", "SELCAN", "SEDA",
			"ELÇİM", "GÜLTEKİN  GÜNHAN", "CEMİLE", "ÇİĞDEM", "İSMAİL", "YAVUZ", "KENAN", "SELÇUK", "SALİHA", "SANEM",
			"ÖVGÜ", "ANIL", "YUSUF", "ALPER", "FATMA", "ECE", "GAMZE", "PINAR", "MELTEM", "HALE", "MUSTAFA", "KEMAL",
			"ALİ", "OZAN", "VELİ", "ÇAĞLAR", "SEYFİ", "CEM", "MUHAMMED", "TAHA", "RECEP", "GANİ", "MAHMUT", "NURİ",
			"HALİL", "İBRAHİM", "ESEN", "İBRAHİM", "UMUT", "SİNAN", "İBRAHİM", "BARIŞ", "MUSTAFA", "KÜRŞAT", "SERDAR",
			"BORA", "CEM", "İNAN", "MİNE", "CANSU", "UMUT", "SEDA", "CEMİLE", "AYŞE", "DİNÇER", "AYDIN", "AYŞE",
			"AHSEN", "SÜREYYA", "BURCU", "BETÜL", "EMİNE", "MEHMET", "GÖKÇE", "ATİLLA", "SÜLEYMAN", "HASAN", "BİLEN",
			"ŞEYMA", "MELİHA", "HACİ", "HALİL", "SERHAT", "BURKAY", "ŞEREF", "CAN", "AYŞE", "NUR", "HACI", "MEHMET",
			"AHMET", "EMRE", "MUSTAFA", "ULAŞ", "EDİP", "GÜVENÇ", "ATİYE", "MELTEM", "EMİNE", "DİLEK", "ESİN", "SEREN",
			"MAHMUT", "ESAT", "FUAT", "ERNİS", "MUZAFFER", "OĞUZ", "TAYYAR", "ALP", "FATIMA", "İLAY", "MEHMET", "CİHAT",
			"KERİME", "RUMEYSA", "HAYRİ", "ÜSTÜN", "AHMET", "TUNÇ", "RAZİYE", "DAMLA", "ONUR", "SALİH", "AYŞE", "CEREN",
			"MUSTAFA", "RAŞİD", "ÖMER", "FARUK", "AHMET", "MERT", "MÜMÜN", "FATİH", "KAAN", "SUAT", "ALİ", "SAİP",
			"ABİDE", "MERVE", "İBRAHİM", "FUAT", "BİLGE", "MERVE", "AHMET", "CEVDET", "MEHMET", "DENİZ", "SAMET",
			"SANCAR", "MUSTAFA", "BUĞRA", "SANEM", "GÖKÇEN", "MERVE", "MUSTAFA", "GÜVENÇ", "OĞUZ", "KAĞAN", "ÖMER",
			"AYKUT", "MEHMET", "ŞİRİN", "ALİ", "RIZA", "BAYRAM", "FURKAN", "ALİ", "RIZA", "EBUBEKİR", "ONUR", "MEHMET",
			"VEYSEL", "DERYA", "SEMA", "MUHAMMED", "MUCAHİD", "MUHAMMED", "FATİH", "MUSTAFA", "ASIM", "YUNUS", "EMRE",
			"KEMAL", "KÜRŞAT", "MEHMET", "SUPHİ", "ASİYE", "BURCU", "YÜKSEL", "UĞUR", "SALİHA", "DİLEK", "NUR",
			"ALEYNA", "MUHAMMED", "FURKAN", "MEHMET", "VEHBİ", "MEHMET", "İKBAL", "İHSAN", "BURAK", "ENES", "TAHİR",
			"ALİ", "MUHARREM", "ELİF", "CEREN", "AYŞE", "GİZEM", "DİDEM", "AYŞE", "AYŞE", "GÜLÇİN", "NURİ", "ANIL",
			"ŞERİFE", "YASEMİN", "MUHAMMED", "YUSUF", "MEHMET", "ALİ", "MELİKE", "ELİF", "MEHMET", "ALİ", "ÖMER",
			"FARUK", "YUSUF", "ZİYA", "MERT", "METİN", "MEHMET", "AKİF", "AHMET", "BURAK", "IŞIK", "MELİKE", "MEHMET",
			"RAŞİT", "GÜLDEN", "SİNEM", "ABDUL", "AZİM", "MEHMET", "HAZBİN", "İSMAİL", "ŞENOL", "CEM", "ATAKAN",
			"VOLKAN ", "MUHAMMED ", "MEHMET", "EVREN", "ŞERİFE", "SEÇİL", "VAZİR", "AKBER", "ALİ", "SAİD", "ÜMMÜ",
			"GÜLSÜM", "ZEYNEP", "EZGİ", "MEHMET", "VEHBİ", "HACI", "KEMAL", "MAKBULE", "SEDA", "FATMA", "BEGÜM",
			"BİLAL", "BARIŞ", "VAHDETTİN", "TALHA", "TAYLAN", "UĞUR", "ERHAN", "HÜSEYİN", "YAKUP", "İLKER", "HALİL",
			"İBRAHİM", "HALİL", "CAN", "CEM", "ÖZGÜR", "VOLKAN", "ONUR", "MEHMET", "DİRİM", "AYŞE", "PINAR", "AHMET",
			"CAN", "ÖMER", "FARUK", "MEHMET", "NURİ", "ALİ", "KEMAL", "İRFAN", "YILDIRIM", "ÖMER", "GÖKHAN", "ŞERİFE",
			"EZGİ", "EYÜP", "GÖKHAN", "MUSTAFA", "ABDULLAH", "OSMAN", "BİLGE", "HASAN", "HÜSEYİN", "HASAN", "CIVAN",
			"MEHMET", "AKİF", "RUKİYE", "ÖZDEN", "MUSTAFA", "CİHAD", "İBRAHİM", "HALİL", "NAİME", "SILA", "MUHAMMET",
			"MUSTAFA", "TAHSİN", "BATUHAN", "EMİNE", "AYÇA", "AHMET", "ÇAĞRI", "SÜHEYLA", "AYÇA", "MELİHA", "ESRA",
			"HALİL", "İBRAHİM", "FATMA", "BETÜL", "AHMET", "BİLGEHAN", "HİKMET", "EKİN", "MUSACİDE", "ZEHRA", "MAHMUT",
			"BAKIR", "ELİF", "TUĞÇE", "ASLIHAN", "ESRA", "MEHMET", "NURİ", "ELİF", "AYŞE", "ERKAN", "SABRİ", "BÜŞRA",
			"SULTAN", "MUSTAFA", "ALİCAN", "ZEKERİYA", "ERSİN", "ABDURRAHMAN", "FUAT", "GÖZDE", "KÜBRA", "MUHAMMED",
			"HASAN", "HÜSEYİN", "ONUR", "ORGÜL", "DERYA", "HASAN", "KADİR", "ŞADİYE", "SELİN", "ÖMER", "FARUK", "ALİ",
			"HASAN", "HACI", "HASAN", "MAHMUT", "SAMİ", "SÜLEYMAN", "SERDAR", "HANİFE", "TUĞÇE", "MEHMET", "YAVUZ",
			"FATMA", "EFSUN", "MEHMET", "ALİ", "YAVUZ", "SELİM", "İDRİS", "BUGRA", "MERVE", "NUR", "MİHRİMAH", "SELCEN",
			"MEHMET", "AKİF", "MEHMED", "UĞUR", "AHMET", "SERKAN", "MEHMET", "YILDIRIM", "GÜLLÜ", "SELCEN", "ÖMER",
			"FARUK", "MEHMET", "ZİYA", "AHMET", "SERCAN", "HALİL", "İBRAHİM", "FATİH", "MEHMET", "MUHAMMET", "BAĞBUR",
			"YUSUF", "ZİYA", "ORHAN", "UYGAR", "AHMET", "ÇAĞRI", "HASAN", "YAVUZHAN", "MUHAMMED", "SAMİ", "METANET",
			"MEHRALİ", "EZGİ", "GİZEM", "MEHMET", "SELAHATTİN", "RAHİME", "MERVE", "MEHMET", "EMRAH", "EMRE", "MERTER",
			"AHMET", "MELİH", "ÖKKEŞ", "YILMAZ", "MURAT", "VOLKAN", "FATMA", "DUYGU", "MUSTAFA", "TURAN", "RAHMİ",
			"TUNA", "YUSUF", "EMRE", "HACI", "ÖMER", "MEHMET", "FATİH", "ALİ", "BURÇ", "SİNAN", "DİNÇER", "MEHMET",
			"NEDİM", "OSMAN", "SALİH", "MAHMUT", "EMRE", "ÖMER", "FARUK", "MEHMET", "FURKAN", "MAHMUT", "BURAK",
			"NİMET", "DİDEM", "ŞÜKRİYE", "TUĞÇE", "REŞİT", "VOLKAN", "SELİM", "OZAN", "AHMAD", "TAHER", "SELKAN",
			"MURAD", "HÜLYA", "GÖZDE", "AHMET", "FURKAN", "TOLGA", "CAN", "EMİR", "MURAT", "ELİF", "NUR", "AHMET",
			"ÇAĞRI", "MERVE", "SETENAY", "ERDEM", "CAN", "MUSTAFA", "KEMAL", "BURÇİN", "MERYEM", "MAHMUT", "ARDA",
			"AHMET", "KÜRŞAD", "DENİZ", "ARMAĞAN", "SAADET", "NİLAY", "HÜSEYİN", "KALKAN", "YAVUZ", "SELİM", "YAVUZ",
			"SELİM", "MEHMET", "FATİH", "MEHMET", "KORAY", "EMİR", "KAAN", "MEHMET", "HİLMİ", "GÖZDE", "GİZEM", "AYŞE",
			"GÜL", "YAŞAR", "BARBAROS", "ELİF", "NİHAL", "YAKUP", "ONUR", "ÇAĞRI", "SERDAR", "MEHMET", "ALİ", "FATİH",
			"AVNİ", "ÖKKEŞ", "CELİL", "HİSAR", "CAN", "AYLİN", "SEVİL", "NECMİYE", "GÜL", "ERTUNÇ", "ONUR", "AHMET",
			"BURAK", "YİĞİT", "CAN", "FATİH", "MEHMET", "METE", "CAN", "HALİL", "CANSUN", "FATMA", "ESRA", "YUNUS",
			"EMRE", "NAZLI", "HİLAL", "HATİCE", "ÖZGE", "HAYATİ", "CAN", "HALİL", "İBRAHİM", "ZEKİYE", "SEVAL",
			"MUHAMMED", "NURİ", "MEHMET", "ALİ", "SABRİ", "SEFA", "RABİA", "ŞEBNEM", "YAŞAR", "GÖZDE", "EMRE", "KAĞAN",
			"ÖĞÜT", "AYDIN", "KUCUR", "TÜLÜBAŞ", "AYDIN", "BADILLIOĞLU", "GÜLEN", "AKKÜÇÜK", "AKCAN", "ATASOY",
			"SARAÇOĞLU", "ÇEKİÇ", "YILMAZ", "İNAL", "YILDIZ", "ALTUN", "ÖZKURT", "PÜRCÜ", "ÖZÇELİK", "ORAL", "KUTLUCAN",
			"BAĞCI", "GEDİK", "YILMAZ", "YİĞİT", "KUPLAY", "KANIK", "YÜKSEK", "KULAK", "GEDİK", "AKCAN", "PAKSOY",
			"KILIÇ", "YILDIRIM", "BAŞKAN", "VURALKAN", "ŞATIR", "ERTEM", "ÖNDER", "SİVİŞ", "AKCA", "ÇAĞLAR", "ERDEM",
			"ÖZCAN", "USTA", "GÜÇ", "TANRIVERDİ", "YILMAZ", "YILMAZ", "ÇİFTDOĞAN", "GAZETECİ", "TEKİN", "KARAKUŞ",
			"EPÇAÇAN", "TİMURTAŞ", "DAYAR", "AMİROVA", "UÇAN", "POLAT", "ÇİÇEK", "ALKURT", "KAYIKÇI", "GENÇ", "TALAS",
			"DEMİR", "GÖÇMEN", "ÖZEKLİ", "MISIRLIOĞLU", "ŞAHİN", "DUYAR", "ÖNEY", "KURNAZ", "AY", "GÜNEY", "ÖZMEN",
			"SÜNER", "ZORLU", "KARAYİĞİT", "SADİ", "AYKAN", "SALMAN", "SEVER", "CİRİT", "KOÇER", "SORGUN", "EVCİLİ",
			"KIYAK", "YILMAZ", "ARIKAN", "YORGUN", "KOCAKAYA", "ALTUNDAL", "ERGİNTÜRK", "ACAR", "KARACAN", "ERŞEKERCİ",
			"ERGÜLÜ", "EŞMEN", "ÖVEN", "USTAALİOĞLU", "ÖĞÜTMEN", "KOÇ", "BAKANAY", "ÖZTÜRK", "GÜVEN", "MEŞE", "DURÇ",
			"ÖZTÜRK", "İNER", "KÖKSAL", "KISA", "KARAKAYA", "KABİL", "KUCUR", "MESCİ", "HAFTACI", "YAZICI", "EROL",
			"KAYMAN", "KÖSE", "ONAR", "ŞEKERCİ", "SÜRMEN", "AKYOL", "VEZİROĞLU", "BİRDANE", "DALYAN", "CİLO", "SARICA",
			"DAROL", "ÖZER", "ÇELİK", "ALPSAN", "GÖKMEN", "ÖNAL", "MUSALAR", "SU", "KURT", "YILMAZ", "GELEBEK", "ÖZAN",
			"SANHAL", "CILIZ", "BASHEER", "ÖZDEMİR", "AKDUR", "SU", "DUR", "KALYONCU", "UÇAR", "KURT", "GÜNEY", "İNCİ",
			"KENAR", "AKBAŞ", "ÖNCEL", "ÖZDOĞAN", "KAVZOĞLU", "ATEŞ", "BUDAK", "KARACAN ", "GÜL", "ÖZMEN", "AK",
			"YILDIRIM", "ÖZAVCI", "AYGÜN", "BATGİ", "AZARKAN", "TÜRKMEN", "ALBAYRAK", "FINDIK", "GÜVENDİ", "YEGEN",
			"YILMAZ", "YILDIZ", "BULUT", "TEL", "KANKILIÇ", "YAVUZ", "SEZGİN", "CAN", "BORAN", "GÜLENAY", "SÜMER",
			"ORDULU", "ŞAHİN", "KARAKÖK", "GÜNGÖR", "GÜNGÖR", "UZUNOĞLU", });

}
