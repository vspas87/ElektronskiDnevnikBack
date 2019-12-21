package BackEndProject.BackEndProject.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class FileHandlerImpl implements FileHandler {
	
	// definisanje foldera u kom će se čuvati logovi
	private static String UPLOADED_FOLDER = "C:\\Users\\Vesna Sovilj\\Desktop\\Project_2";
	
	@Override
	public String singleFileUpload(MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return"redirect:uploadStatus";
	}
	try {
		// dobijanje fajla i čuvanje na izabranoj lokaciji
		byte[] bytes = file.getBytes();
		Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
		Files.write(path, bytes);
		redirectAttributes.addFlashAttribute("message",	"You successfully uploaded '"+ file.getOriginalFilename() + "'");
	} catch	(IOException e) {
		throw e;
	}
	return "redirect:/uploadStatus";
	}	
	

}
