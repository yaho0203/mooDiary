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
      <div className="diary-form-header">
        <h2 className="handwriting">새 일기 작성</h2>
        <p className="diary-date">{new Date().toLocaleDateString('ko-KR', { 
          year: 'numeric', 
          month: 'long', 
          day: 'numeric',
          weekday: 'long'
        })}</p>
      </div>
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="content" className="handwriting">오늘의 일기</label>
          <textarea
            id="content"
            value={content}
            onChange={handleContentChange}
            placeholder="오늘 하루는 어땠나요? 자유롭게 작성해보세요..."
            rows={8}
            disabled={isLoading}
            className="diary-textarea"
          />
        </div>

        <div className="form-group">
          <label className="handwriting">이미지 첨부 (선택사항)</label>
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
              <div className="upload-icon">📷</div>
              <p className="handwriting">이미지를 드래그하거나 클릭하여 업로드</p>
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
            className="diary-button"
          >
            {isLoading ? (
              <>
                <span className="diary-spinner"></span> 분석 중...
              </>
            ) : (
              '일기 저장'
            )}
          </button>
        </div>
      </form>

      <style>{`
        .diary-form {
          max-width: 700px;
          margin: 0 auto;
          padding: 30px;
          background: #fefcf7;
          border: 2px solid #d4c4a8;
          border-radius: 12px;
          box-shadow: 
            0 0 0 1px #e8dcc0,
            0 4px 20px rgba(93, 78, 55, 0.15);
          position: relative;
        }

        .diary-form::before {
          content: '';
          position: absolute;
          left: 20px;
          top: 0;
          bottom: 0;
          width: 2px;
          background: linear-gradient(to bottom, 
            transparent 0%, 
            #c4b59a 20%, 
            #c4b59a 80%, 
            transparent 100%);
        }

        .diary-form-header {
          text-align: center;
          margin-bottom: 30px;
          padding-bottom: 20px;
          border-bottom: 2px solid #e8dcc0;
        }

        .diary-form h2 {
          margin: 0 0 10px 0;
          color: #8b7355;
          font-size: 1.8rem;
        }

        .form-group {
          margin-bottom: 25px;
        }

        .form-group label {
          display: block;
          margin-bottom: 10px;
          font-weight: 600;
          color: #5d4e37;
          font-size: 1.1rem;
        }

        .image-upload-area {
          border: 2px dashed #d4c4a8;
          border-radius: 12px;
          padding: 25px;
          text-align: center;
          transition: all 0.3s ease;
          cursor: pointer;
          background: #fefcf7;
        }

        .image-upload-area:hover,
        .image-upload-area.drag-active {
          border-color: #c4b59a;
          background-color: #f8f6f0;
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(93, 78, 55, 0.1);
        }

        .upload-label {
          display: block;
          cursor: pointer;
        }

        .upload-placeholder {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 12px;
        }

        .upload-icon {
          font-size: 3rem;
          opacity: 0.7;
        }

        .upload-placeholder p {
          margin: 0;
          color: #8b7355;
          font-size: 1.1rem;
        }

        .upload-hint {
          font-size: 0.9rem;
          color: #a68b5b;
        }

        .image-preview {
          position: relative;
          display: inline-block;
        }

        .image-preview img {
          max-width: 250px;
          max-height: 250px;
          border-radius: 12px;
          object-fit: cover;
          border: 2px solid #d4c4a8;
          box-shadow: 0 4px 12px rgba(93, 78, 55, 0.2);
        }

        .remove-image-btn {
          position: absolute;
          top: -10px;
          right: -10px;
          width: 28px;
          height: 28px;
          border: none;
          border-radius: 50%;
          background-color: #dc3545;
          color: white;
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 14px;
          font-weight: bold;
          box-shadow: 0 2px 4px rgba(220, 53, 69, 0.3);
          transition: all 0.3s ease;
        }

        .remove-image-btn:hover {
          background-color: #c82333;
          transform: scale(1.1);
        }

        .remove-image-btn:disabled {
          background-color: #9ca3af;
          cursor: not-allowed;
          transform: none;
        }

        .form-actions {
          text-align: center;
          margin-top: 30px;
        }

        .diary-button {
          background: linear-gradient(135deg, #e8dcc0 0%, #d4c4a8 100%);
          border: 2px solid #c4b59a;
          border-radius: 25px;
          padding: 15px 30px;
          font-family: 'Quicksand', sans-serif;
          font-weight: 600;
          color: #5d4e37;
          cursor: pointer;
          transition: all 0.3s ease;
          box-shadow: 0 4px 8px rgba(93, 78, 55, 0.2);
          font-size: 1.1rem;
          display: inline-flex;
          align-items: center;
          gap: 8px;
        }

        .diary-button:hover:not(:disabled) {
          background: linear-gradient(135deg, #d4c4a8 0%, #c4b59a 100%);
          transform: translateY(-2px);
          box-shadow: 0 6px 12px rgba(93, 78, 55, 0.3);
        }

        .diary-button:active {
          transform: translateY(0);
          box-shadow: 0 2px 4px rgba(93, 78, 55, 0.2);
        }

        .diary-button:disabled {
          background: #e8dcc0;
          border-color: #d4c4a8;
          color: #a68b5b;
          cursor: not-allowed;
          transform: none;
          box-shadow: 0 2px 4px rgba(93, 78, 55, 0.1);
        }

        .diary-spinner {
          display: inline-block;
          width: 16px;
          height: 16px;
          border: 2px solid #e8dcc0;
          border-radius: 50%;
          border-top-color: #c4b59a;
          animation: spin 1s ease-in-out infinite;
        }

        @keyframes spin {
          to { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};

export default DiaryForm;
