import React, { useState } from 'react';

interface DiaryFormProps {
  onSubmit: (content: string, imageFile?: File) => void;
  isLoading?: boolean;
}

const DiaryForm: React.FC<DiaryFormProps> = ({ onSubmit, isLoading = false }) => {
  const [content, setContent] = useState('');
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [dragActive, setDragActive] = useState(false);

  const handleContentChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setContent(e.target.value);
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      handleImageFile(file);
    }
  };

  const handleImageFile = (file: File) => {
    // 파일 형식 검증
    if (!file.type.startsWith('image/')) {
      alert('이미지 파일만 업로드 가능합니다.');
      return;
    }

    // 파일 크기 검증 (10MB)
    if (file.size > 10 * 1024 * 1024) {
      alert('파일 크기가 너무 큽니다. 최대 10MB까지 업로드 가능합니다.');
      return;
    }

    setImageFile(file);
    
    // 이미지 미리보기 생성
    const reader = new FileReader();
    reader.onload = (e) => {
      setImagePreview(e.target?.result as string);
    };
    reader.readAsDataURL(file);
  };

  const handleDrag = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    
    const file = e.dataTransfer.files?.[0];
    if (file) {
      handleImageFile(file);
    }
  };

  const removeImage = () => {
    setImageFile(null);
    setImagePreview(null);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!content.trim()) {
      alert('일기 내용을 입력해주세요.');
      return;
    }
    onSubmit(content, imageFile || undefined);
  };

  return (
    <div className="diary-form">
      <h2>새 일기 작성</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="content">오늘의 일기</label>
          <textarea
            id="content"
            value={content}
            onChange={handleContentChange}
            placeholder="오늘 하루는 어땠나요? 자유롭게 작성해보세요..."
            rows={6}
            disabled={isLoading}
          />
        </div>

        <div className="form-group">
          <label>이미지 첨부 (선택사항)</label>
          <div
            className={`image-upload-area ${dragActive ? 'drag-active' : ''}`}
            onDragEnter={handleDrag}
            onDragLeave={handleDrag}
            onDragOver={handleDrag}
            onDrop={handleDrop}
          >
            <input
              type="file"
              id="image"
              accept="image/*"
              onChange={handleImageChange}
              disabled={isLoading}
              style={{ display: 'none' }}
            />
            <label htmlFor="image" className="upload-label">
              {imagePreview ? (
                <div className="image-preview">
                  <img src={imagePreview} alt="미리보기" />
                  <button
                    type="button"
                    onClick={removeImage}
                    className="remove-image-btn"
                    disabled={isLoading}
                  >
                    ✕
                  </button>
                </div>
              ) : (
            <div className="upload-placeholder">
              <div className="upload-icon">이미지</div>
              <p>이미지를 드래그하거나 클릭하여 업로드</p>
              <p className="upload-hint">JPG, PNG, GIF (최대 10MB)</p>
            </div>
              )}
            </label>
          </div>
        </div>

        <div className="form-actions">
          <button
            type="submit"
            disabled={isLoading || !content.trim()}
            className="submit-btn"
          >
            {isLoading ? '분석 중...' : '일기 저장'}
          </button>
        </div>
      </form>

      <style>{`
        .diary-form {
          max-width: 600px;
          margin: 0 auto;
          padding: 20px;
          background: #fff;
          border-radius: 12px;
          box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .diary-form h2 {
          margin-bottom: 20px;
          color: #333;
          text-align: center;
        }

        .form-group {
          margin-bottom: 20px;
        }

        .form-group label {
          display: block;
          margin-bottom: 8px;
          font-weight: 600;
          color: #555;
        }

        textarea {
          width: 100%;
          padding: 12px;
          border: 2px solid #e1e5e9;
          border-radius: 8px;
          font-size: 16px;
          font-family: inherit;
          resize: vertical;
          transition: border-color 0.3s ease;
        }

        textarea:focus {
          outline: none;
          border-color: #4f46e5;
        }

        textarea:disabled {
          background-color: #f5f5f5;
          cursor: not-allowed;
        }

        .image-upload-area {
          border: 2px dashed #d1d5db;
          border-radius: 8px;
          padding: 20px;
          text-align: center;
          transition: all 0.3s ease;
          cursor: pointer;
        }

        .image-upload-area:hover,
        .image-upload-area.drag-active {
          border-color: #4f46e5;
          background-color: #f8fafc;
        }

        .upload-label {
          display: block;
          cursor: pointer;
        }

        .upload-placeholder {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 8px;
        }

        .upload-icon {
          font-size: 48px;
          opacity: 0.6;
        }

        .upload-placeholder p {
          margin: 0;
          color: #6b7280;
        }

        .upload-hint {
          font-size: 14px;
          color: #9ca3af;
        }

        .image-preview {
          position: relative;
          display: inline-block;
        }

        .image-preview img {
          max-width: 200px;
          max-height: 200px;
          border-radius: 8px;
          object-fit: cover;
        }

        .remove-image-btn {
          position: absolute;
          top: -8px;
          right: -8px;
          width: 24px;
          height: 24px;
          border: none;
          border-radius: 50%;
          background-color: #ef4444;
          color: white;
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 12px;
        }

        .remove-image-btn:hover {
          background-color: #dc2626;
        }

        .remove-image-btn:disabled {
          background-color: #9ca3af;
          cursor: not-allowed;
        }

        .form-actions {
          text-align: center;
        }

        .submit-btn {
          background-color: #4f46e5;
          color: white;
          border: none;
          padding: 12px 24px;
          border-radius: 8px;
          font-size: 16px;
          font-weight: 600;
          cursor: pointer;
          transition: background-color 0.3s ease;
        }

        .submit-btn:hover:not(:disabled) {
          background-color: #4338ca;
        }

        .submit-btn:disabled {
          background-color: #9ca3af;
          cursor: not-allowed;
        }
      `}</style>
    </div>
  );
};

export default DiaryForm;
