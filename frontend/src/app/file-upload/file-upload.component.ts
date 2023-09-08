import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent {
  formData: FormData = new FormData();
  apiUrl: string = 'http://localhost:9090/upload/file';

  constructor(private http: HttpClient) { }

  handleFileInput(event: any) {
    const file: File = event.target.files[0];
    this.formData = new FormData();
    this.formData.append('filedata', file);
  }

  submitForm() {
    // Create a new FormData object
    const formData = new FormData();

    // Append all form field values directly to the new FormData object
    this.formData.append('name', 'name');
    this.formData.append('nodeType', 'nodeType');
    this.formData.append('relativePath', 'relativePath');
    this.formData.append('cm:description', 'cm:description');
    this.formData.append('cm:title', 'cm:title');
    this.formData.append('tag', 'tag');
    this.formData.append('username', 'username');
    this.formData.append('password', 'password');
    this.formData.append('filedata', "filedata");
    console.log(this.formData)
    // Make a POST request to the API
    this.http.post(this.apiUrl, formData).subscribe({
      next: (response) => {
        console.log(response);
        // Handle success response here
      },
      error: (error) => {
        console.error(error);
        // Handle error response here
      }
    });
  }
}
